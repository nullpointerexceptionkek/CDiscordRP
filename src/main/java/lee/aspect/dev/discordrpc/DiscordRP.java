package lee.aspect.dev.discordrpc;

import lee.aspect.dev.application.RunLoopManager;
import lee.aspect.dev.discordipc.IPCClient;
import lee.aspect.dev.discordipc.IPCListener;
import lee.aspect.dev.discordipc.entities.Callback;
import lee.aspect.dev.discordipc.entities.RichPresence;
import lee.aspect.dev.discordipc.exceptions.NoDiscordClientException;
import lee.aspect.dev.discordrpc.settings.Settings;

import java.util.Calendar;


public class DiscordRP {

    public static final boolean autoReconnect = true;
    public static IPCClient client;
    public static Callback callback;
    private long created = -1;
    private long current;

    public void LaunchReadyCallBack(Updates updates) throws NoDiscordClientException {
        Calendar calendar = Calendar.getInstance();
        switch (Script.getTimestampmode()) {
            case appLaunch:
                this.created = System.currentTimeMillis();
                break;
            case none:
                created = -1;
                break;

            case localTime:
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                this.created = calendar.getTimeInMillis();
                break;

            case cdFromDayEnd:
                calendar.set(Calendar.HOUR_OF_DAY, 24);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                this.created = calendar.getTimeInMillis();
                break;
            case custom:
                //TODO: Custom Time
                break;
        }
        created = (long) Math.floor(created / 1000f);
        current = (long) Math.floor(System.currentTimeMillis() / 1000f);

        /*
         * official discord ipc doc
         * https://discord.com/developers/docs/topics/rpc#rpc
         */

        callback = new Callback();
        setIPCClient(updates);

        client.connect();
    }


    public void shutdown() {
        client.close();
    }


    public void update(Updates updates) {
        try {
            setBuilder(updates, client);
        } catch (IllegalStateException | NullPointerException e) {
            if (autoReconnect && RunLoopManager.isRunning) {
                setIPCClient(updates);
                try {
                    client.connect();
                } catch (NoDiscordClientException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private void setBuilder(Updates updates, IPCClient client) {
        RichPresence.Builder builder = new RichPresence.Builder();
        builder.setState(updates.getSl())
                .setDetails(updates.getFl())
                .setLargeImage(updates.getImage(), updates.getImagetext())
                .setSmallImage(updates.getSmallimage(), updates.getSmalltext())
                .setButton1Text(updates.getButton1Text())
                .setButton1Url(updates.getButton1Url())
                .setButton2Text(updates.getButton2Text())
                .setButton2Url(updates.getButton2Url());
        if (!(created == -1)) {
            if(Script.getTimestampmode() == Script.TimeStampMode.sinceUpdate){
                builder.setStartTimestamp((long) Math.floor(System.currentTimeMillis() / 1000f));
            }
            else {
                if (created > current) {
                    builder.setEndTimestamp(created);
                } else {
                    builder.setStartTimestamp(created);
                }
            }
        }
        client.sendRichPresence(builder.build(), callback);
    }

    private void setIPCClient(Updates updates) {
        client = new IPCClient(Long.parseLong(Settings.getDiscordAPIKey()));
        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client) {
                setBuilder(updates, client);
            }
        });
    }


}

