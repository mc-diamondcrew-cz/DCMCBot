package me.pljr.dcmcbot.managers;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.objects.Ticket;
import me.pljr.pljrapibungee.database.DataSource;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QueryManager {
    private final DataSource dataSource;
    private final Guild guild;

    public QueryManager(DataSource dataSource, Guild guild){
        this.dataSource = dataSource;
        this.guild = guild;
    }

    public HashMap<Long, Ticket> loadTickets(){
        HashMap<Long, Ticket> tickets = new HashMap<>();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM dcmcbot_tickets"
            );
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                long userId = resultSet.getLong("userId");
                long channelId = resultSet.getLong("channelId");
                Member member = guild.getMemberById(userId);
                TextChannel channel = guild.getTextChannelById(channelId);
                if (member == null || channel == null) continue;
                tickets.put(channelId, new Ticket(member, channelId));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        updateTickets(tickets);
        return tickets;
    }

    public void updateTickets(HashMap<Long, Ticket> tickets){
        new Thread(() -> {
            try {
                Connection deleteConnection = dataSource.getConnection();
                PreparedStatement deleteStatement = deleteConnection.prepareStatement(
                        "DELETE FROM dcmcbot_tickets"
                );
                deleteStatement.executeUpdate();
                dataSource.close(deleteConnection, deleteStatement, null);

                for (Map.Entry<Long, Ticket> entry : tickets.entrySet()){
                    Connection connection = dataSource.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO  dcmcbot_tickets VALUES (?,?)"
                    );
                    preparedStatement.setLong(1, entry.getValue().getMember().getIdLong());
                    preparedStatement.setLong(2, entry.getKey());
                    preparedStatement.executeUpdate();
                    dataSource.close(connection, preparedStatement, null);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }).start();
    }

    public HashMap<UUID, Long> loadAccounts(){
        HashMap<UUID, Long> accounts = new HashMap<>();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM dcmcbot_accounts"
            );
            ResultSet results = statement.executeQuery();
            while (results.next()){
                accounts.put(UUID.fromString(results.getString("uuid")), results.getLong("userId"));
            }
            dataSource.close(connection, statement, results);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return accounts;
    }

    public void saveAccounts(){
        try {
            Connection deleteConnection = dataSource.getConnection();
            PreparedStatement deleteStatement = deleteConnection.prepareStatement(
                    "DELETE FROM dcmcbot_accounts"
            );
            deleteStatement.executeUpdate();
            dataSource.close(deleteConnection, deleteStatement, null);

            DCAccountManager manager = DCMCBot.getDcAccountManager();

            HashMap<UUID, Long> accounts = manager.getAccounts();
            for (Map.Entry<UUID, Long> entry : accounts.entrySet()){
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO dcmcbot_accounts VALUES (?,?)"
                );
                statement.setString(1, entry.getKey().toString());
                statement.setString(2, entry.getValue().toString());
                statement.executeUpdate();
                dataSource.close(connection, statement, null);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void setupTables(){
        try {
            Connection connectionTickets = dataSource.getConnection();
            PreparedStatement statementTickets = connectionTickets.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS dcmcbot_tickets (" +
                            "userId char(18) NOT NULL PRIMARY KEY," +
                            "channelId char(18) NOT NULL);"
            );
            statementTickets.executeUpdate();
            dataSource.close(connectionTickets, statementTickets, null);

            Connection connectionAccounts = dataSource.getConnection();
            PreparedStatement statementAccounts = connectionAccounts.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS dcmcbot_accounts (" +
                            "uuid char(36) NOT NULL," +
                            "userId char(18) NOT NULL);"
            );
            statementAccounts.executeUpdate();
            dataSource.close(connectionAccounts, statementAccounts, null);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
