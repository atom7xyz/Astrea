package net.herospvp.astrea.common.core.logging;

import net.herospvp.astrea.common.core.Bank;
import net.herospvp.astrea.common.core.logging.elements.ActionsLogger;
import net.herospvp.astrea.common.core.logging.elements.DetectionsLogger;
import net.herospvp.astrea.common.utils.CompactUtils;
import net.herospvp.database.lib.Musician;
import net.herospvp.database.lib.items.Notes;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.concurrent.LinkedBlockingQueue;

public class CommonLogger {

    private final Notes logs, actions;
    private final String[] logFields, actionFields;
    private final Musician musician;

    private final LinkedBlockingQueue<DetectionsLogger> detectionsLoggerQueue;
    private final LinkedBlockingQueue<ActionsLogger> actionsLoggerQueue;

    private final Bank bank;

    public CommonLogger(
            @NotNull CompactUtils compactUtils
    ) {
        this.logs = new Notes("astrea_logs");
        this.actions = new Notes("astrea_actions");
        this.logFields = new String[] { "time", "user", "ping", "tps", "ip", "detection" };
        this.actionFields = new String[] { "time", "user", "ping", "tps", "ip", "id", "reason" };
        this.musician = compactUtils.getAstrea().getMusician();
        this.bank = compactUtils.getBank();
        this.detectionsLoggerQueue = new LinkedBlockingQueue<>();
        this.actionsLoggerQueue = new LinkedBlockingQueue<>();
    }

    public void offer(
            @NotNull DetectionsLogger detectionsLogger
    ) {
        detectionsLoggerQueue.offer(detectionsLogger);
    }

    public void offer(
            @NotNull ActionsLogger actionsLogger
    ) {
        actionsLoggerQueue.offer(actionsLogger);
    }

    public void startup() {
        musician.offer((connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        logs.createTable(new String[] {
                                "time TIMESTAMP",
                                "user CHAR(16)",
                                "ping INT UNSIGNED",
                                "tps FLOAT UNSIGNED",
                                "ip CHAR(100)",
                                "detection CHAR(50)"
                        })
                );
                preparedStatement.execute();
                preparedStatement = connection.prepareStatement(
                        actions.createTable(new String[] {
                                "time TIMESTAMP",
                                "user CHAR(16)",
                                "ping INT UNSIGNED",
                                "tps FLOAT UNSIGNED",
                                "ip CHAR(100)",
                                "id CHAR(12)",
                                "reason CHAR(50)"
                        })
                );
                preparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement);
            }
        });
    }

    public void getTotal() {
        musician.offer((connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = connection.prepareStatement(
                        logs.selectAll()
                );
                resultSet = preparedStatement.executeQuery();
                resultSet.last();
                synchronized (bank) {
                    bank.setVl(resultSet.getRow());
                }

                preparedStatement = connection.prepareStatement(
                        actions.selectAll()
                );
                resultSet = preparedStatement.executeQuery();
                resultSet.last();
                synchronized (bank) {
                    bank.setPunishments(resultSet.getRow());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement, resultSet);
            }
        });
    }

    public void save() {
        musician.offer((connection, instrument) -> {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = connection.prepareStatement(
                        logs.pendingInsert(logFields)
                );
                DetectionsLogger detectionsLogger;
                while ((detectionsLogger = detectionsLoggerQueue.poll()) != null) {
                    preparedStatement.setTimestamp(1, new Timestamp(detectionsLogger.getTime()));
                    preparedStatement.setString(2, detectionsLogger.getPlayer().getName());
                    preparedStatement.setInt(3, detectionsLogger.getPlayerPing());
                    preparedStatement.setDouble(4, detectionsLogger.getServerTPS());
                    preparedStatement.setString(5, detectionsLogger.getPlayer().getAddress().getHostString());
                    preparedStatement.setString(6, detectionsLogger.getDetection());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();

                preparedStatement = connection.prepareStatement(
                        actions.pendingInsert(actionFields)
                );
                ActionsLogger actionsLogger;
                while ((actionsLogger = actionsLoggerQueue.poll()) != null) {
                    preparedStatement.setTimestamp(1, new Timestamp(actionsLogger.getTime()));
                    preparedStatement.setString(2, actionsLogger.getPlayer().getName());
                    preparedStatement.setInt(3, actionsLogger.getPlayerPing());
                    preparedStatement.setDouble(4, actionsLogger.getServerTPS());
                    preparedStatement.setString(5, actionsLogger.getPlayer().getAddress().getHostString());
                    preparedStatement.setString(6, actionsLogger.getId());
                    preparedStatement.setString(7, actionsLogger.getReason());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                instrument.close(preparedStatement);
            }
        });
    }

}
