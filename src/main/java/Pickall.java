import entities.Furni;
import gearth.extensions.Extension;
import gearth.extensions.ExtensionInfo;
import gearth.extensions.parsers.*;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import gearth.protocol.packethandler.shockwave.packets.ShockPacketOutgoing;
import javafx.fxml.Initializable;
import parsers.OHFurni;
import parsers.OHItem;

import javax.swing.Timer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ExtensionInfo(
        Title = ":pickall",
        Description = "Pickall items in the room.",
        Version = "1.0",
        Author = "Thauan"
)

public class Pickall extends Extension {

    public Pickall(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        new Pickall(args).run();
    }

    public static Pickall RUNNING_INSTANCE;

    public String roomId;

    public List<Furni> roomFurnis = new ArrayList<>();


    @Override
    protected void onStartConnection() {
    }

    @Override
    protected void initExtension() {
        RUNNING_INSTANCE = this;

        onConnect((host, port, APIVersion, versionClient, client) -> {
            if (!Objects.equals(versionClient, "SHOCKWAVE")) {
                System.exit(0);
            }
        });

        intercept(HMessage.Direction.TOCLIENT, "ITEMS", this::onItems);
        intercept(HMessage.Direction.TOCLIENT, "ACTIVEOBJECTS", this::onActiveObjects);
        intercept(HMessage.Direction.TOCLIENT, "ACTIVEOBJECT_ADD", this::onActiveObjectsAdd);
        intercept(HMessage.Direction.TOCLIENT, "FLATINFO", this::onFlatInfo);
        intercept(HMessage.Direction.TOSERVER, "CHAT", this::onChat);

    }



    void onFlatInfo(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        String previousRoomId = roomId;
        roomId = hPacket.readString();

        if(Objects.equals(roomId, previousRoomId)) {
            return;
        }

        roomFurnis.clear();
    }

    private void onItems(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        if(hPacket.length() > 2) {
            OHItem[] items = OHItem.parse(hPacket);

            for (OHItem item : items) {
                Furni newFurni = new Furni(item.getId(), item.getClassName(), "wall");
                roomFurnis.add(newFurni);
            }
        }
    }

    private void onActiveObjectsAdd(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        int furniId = Integer.parseInt(hPacket.readString());
        String className = hPacket.readString();
        Furni newFurni = new Furni(furniId, className, "floor");
        roomFurnis.add(newFurni);
    }

    private void onActiveObjects(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        try {
            OHFurni[] furnis = OHFurni.parse(hPacket);

            for (OHFurni furni : furnis) {
                Furni newFurni = new Furni(furni.getId(), furni.getClassName(), "floor");
                roomFurnis.add(newFurni);
                System.out.println("Furni: " + newFurni.getClassName() + " " + newFurni.getType() + " " + newFurni.getId());
            }
        }catch (Exception ignored) {
        }
    }

    private void onChat(HMessage hMessage) {
        HPacket hPacket = hMessage.getPacket();
        String message = hPacket.readString(StandardCharsets.ISO_8859_1);

        if (message.startsWith(":pickall") && message.split(" ").length == 1) {
            hMessage.setBlocked(true);
            new Thread(() -> {
                pickupItems("all");
            }).start();
        }
        if (message.startsWith(":pickall") && message.split(" ").length > 1) {
            String argument = message.split(" ")[1];
            hMessage.setBlocked(true);
            new Thread(() -> {
                pickupItems(argument);
            }).start();
        }
    }

    public void pickupItems(String argument) {
        Iterator<Furni> iterator = roomFurnis.iterator();

        while (iterator.hasNext()) {
            Furni furni = iterator.next();
            boolean shouldProcess = false;

            if ("wall".equals(argument) || "floor".equals(argument)) {
                shouldProcess = furni.getType().equals(argument);
            } else if (!"all".equals(argument)) {
                shouldProcess = furni.getClassName().equals(argument);
                Set<String> uniqueClassNames = roomFurnis.stream()
                        .filter(furniClass -> "floor".equals(furni.getType()))
                        .map(Furni::getClassName)
                        .collect(Collectors.toSet());

                String classNamesStr = String.join(" ", uniqueClassNames);

                if (!classNamesStr.trim().contains(argument)) {
                    sendToServer(new ShockPacketOutgoing("{out:WHISPER}{s:\" Furnis: " + classNamesStr.trim() + "\"}"));
                    break;
                }
            } else {
                shouldProcess = true;
            }

            if (shouldProcess) {
                sendToServer(new ShockPacketOutgoing("ACnew " + (Objects.equals(furni.getType(), "wall") ? "item" : "stuff") + " " + furni.getId()));
                waitAFckingSec(500);
                iterator.remove();
            }
        }
    }

    public static void waitAFckingSec(int millisecActually) {
        try {
            Thread.sleep(millisecActually);
        } catch (InterruptedException ignored) {
        }
    }

}
