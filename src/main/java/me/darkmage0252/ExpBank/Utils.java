package me.darkmage0252.ExpBank;

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
	}

	public static boolean idIsPlayer(final String idLine, final OfflinePlayer player) {
		Long playerId = plugin.getConfig().getLong("ids.db.uuid" + player.getUniqueId().toString());

		if (playerId == null) {
			return false;
		}

		return playerId == idForLine(idLine);
	}

	public static long idForPlayer(final OfflinePlayer player) {
		long playerId = plugin.getConfig().getLong("ids.db.uuid" + player.getUniqueId().toString());

		if (playerId > 0) {
			return playerId;
		}

		synchronized (plugin) {
			playerId = ExpBank.maxId;
			plugin.getConfig().set("ids.max", ++ExpBank.maxId);
			plugin.getConfig().set("ids.db.uuid" + player.getUniqueId().toString(), playerId);
			plugin.saveConfig();
		}

		return playerId;
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

	/**
	 * Attempt to upgrade an original ExpBank sign to ModulExpBank.
	 * 
	 * @param sign
	 *            the sign
	 */
	public static void attemptToUpgrade(final Sign sign, final Player player) {
		String line = sign.getLine(ExpBankListener.ID_LINE);

		if (line == null || !ChatColor.stripColor(line).startsWith("#")) {
			int expInSign = Integer.parseInt(sign.getLine(3));

			sign.setLine(ExpBankListener.ID_LINE, ChatColor.DARK_GRAY + "#" + idForPlayer(player));
			sign.setLine(ExpBankListener.PLAYER_LINE, colorForExp(expInSign) + player.getName());
			sign.update(true);
			player.sendMessage(String.valueOf(Utils.prefix) + "Conversion en ModulExpBank réussie !");
		}
	}
}
