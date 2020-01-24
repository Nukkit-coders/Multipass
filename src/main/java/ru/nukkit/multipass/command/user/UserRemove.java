package ru.nukkit.multipass.command.user;

import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;
import ru.nukkit.multipass.command.Cmd;
import ru.nukkit.multipass.command.CmdDefine;
import ru.nukkit.multipass.permissions.Users;
import ru.nukkit.multipass.util.Message;

@CmdDefine(command = "user", alias = "userperm", allowConsole = true, subCommands = {"remove|rmv|delete|del", "[A-Za-z0-9 ]{1,15}"}, permission = "multipass.admin", description = Message.CMD_USER_REMOVE)
public class UserRemove extends Cmd {
    @Override
    public boolean execute(CommandSender sender, Player player, String[] args) {
        String id = args[1];

        Users.isRegistered(id).whenComplete((exist, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                if (exist) {
                    Users.remove(id);
                    Message.USER_REMOVE_OK.print(sender, id);
                } else {
                    Message.USER_NOTEXIST.print(sender, id);
                }
            }
        });
        return true;
    }
}
