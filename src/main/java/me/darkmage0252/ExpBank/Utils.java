package me.darkmage0252.ExpBank;

import java.util.UUID;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class Utils {
	static ExpBank plugin;
	static String prefix;
	static boolean PermsEnabled;
	static boolean EconomyEnabled;
	static Permission permissions;

	static {
		Utils.prefix = ChatColor.BLUE + "[ModulExpBank] ";
		Utils.PermsEnabled = ExpBank.PermsEnabled;
		Utils.EconomyEnabled = ExpBank.EconomyEnabled;
		Utils.permissions = ExpBank.permission;
	}

	public static boolean charge(final OfflinePlayer player, final double amt) {
		if (!Utils.EconomyEnabled) {
			return true;
		}
		if (ExpBank.economy.getBalance(player) >= amt) {
			ExpBank.economy.withdrawPlayer(player, amt);
			return true;
		}
		return false;
	}

	public static boolean deposit(final OfflinePlayer player, final double amt) {
		if (!Utils.EconomyEnabled) {
			return true;
		}
		if (ExpBank.economy.hasAccount(player)) {
			ExpBank.economy.depositPlayer(player, amt);
			return true;
		}
		return false;
	}

	public static void collectLevels(final Player player, final Sign sign, final Integer amount, String idLine) {
		player.setLevel(player.getLevel() + Integer.parseInt(sign.getLine(3)));
		player.sendMessage(String.valueOf(Utils.prefix) + "Tu as récupéré tes niveaux");
		if (ExpBank.ReturnAmt != 0.0) {
			deposit(player, ExpBank.ReturnAmt);
			player.sendMessage(String.valueOf(Utils.prefix) + ExpBank.economy.format(ExpBank.ReturnAmt) + " t'ont été rendus");
		}
		plugin.getConfig().set("ids.db.id" + idForLine(idLine), null);
		plugin.saveConfig();
	}

	public static boolean idIsPlayer(final String idLine, final OfflinePlayer player) {
		String uuidString = plugin.getConfig().getString("ids.db.id" + idForLine(idLine));

		if (uuidString == null) {
			return false;
		}

		UUID uuid = UUID.fromString(uuidString);
		return player.getUniqueId().equals(uuid);
	}

	public static long idForLine(String idLine) {
		return Long.parseLong(idLine.substring(idLine.lastIndexOf("#") + 1));
	}

	public static ChatColor colorForExp(final int exp) {
		if (exp >= ExpBank.LevelsMax) {
			return ChatColor.YELLOW;
		} else if (exp == 0) {
			return ChatColor.GREEN;
		} else {
			return ChatColor.BLUE;
		}
	}

	public static boolean hasPerm(final Player player, final String perm) {
		if (Utils.PermsEnabled) {
			return Utils.permissions.has(player, perm);
		}
		return player.isOp();
	}
}
