package me.darkmage0252.ExpBank;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import net.milkbowl.vault.permission.Permission;

public class Utils
{
    static ExpBank plugin;
    static String prefix;
    static boolean PermsEnabled;
    static boolean EconomyEnabled;
    static Permission permissions;
    
    static {
        Utils.prefix = ChatColor.BLUE + "[ExpBank]";
        Utils.PermsEnabled = ExpBank.PermsEnabled;
        Utils.EconomyEnabled = ExpBank.EconomyEnabled;
        Utils.permissions = ExpBank.permission;
    }
    
    public static boolean charge(final String player, final double amt) {
        if (!Utils.EconomyEnabled) {
            return true;
        }
        if (ExpBank.economy.getBalance(player) >= amt) {
            ExpBank.economy.withdrawPlayer(player, amt);
            return true;
        }
        return false;
    }
    
    public static boolean deposit(final String player, final double amt) {
        if (!Utils.EconomyEnabled) {
            return true;
        }
        if (ExpBank.economy.hasAccount(player)) {
            ExpBank.economy.depositPlayer(player, amt);
            return true;
        }
        return false;
    }
    
    public static void collectLevels(final Player player, final Sign sign, final Integer amount) {
        player.setLevel(player.getLevel() + Integer.parseInt(sign.getLine(3)));
        player.sendMessage(String.valueOf(Utils.prefix) + " You have collected your levels.");
        if (ExpBank.ReturnAmt != 0.0) {
            deposit(player.getName(), ExpBank.ReturnAmt);
            player.sendMessage(String.valueOf(Utils.prefix) + " " + ExpBank.ReturnAmt + " has been returned to you.");
        }
    }
    
    public static boolean hasPerm(final Player player, final String perm) {
        if (Utils.PermsEnabled) {
            return Utils.permissions.has(player, perm);
        }
        return player.isOp();
    }
}
