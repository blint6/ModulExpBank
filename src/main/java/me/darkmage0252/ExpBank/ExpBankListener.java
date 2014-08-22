package me.darkmage0252.ExpBank;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExpBankListener implements Listener {
	private static final int PLAYER_LINE = 1;
	private static final int ID_LINE = 2;
	public ExpBank plugin;
	String prefix;

	public ExpBankListener(final ExpBank instance) {
		super();
		this.prefix = ChatColor.BLUE + "[ExpBank]";
		this.plugin = instance;
	}

	@EventHandler
	public void onSignChange(final SignChangeEvent event) {
		final Player player = event.getPlayer();
		if (event.getLine(0).equalsIgnoreCase(ChatColor.stripColor("[expbank]"))) {
			if (Utils.hasPerm(player, "expbank.create")) {
				if (Utils.charge(player, ExpBank.CreateAmt)) {
					long id = ExpBank.maxId;
					plugin.getConfig().set("ids.max", ++ExpBank.maxId);
					plugin.getConfig().set("ids.db.id" + id, player.getUniqueId().toString());
					plugin.saveConfig();

					event.setLine(0, "[ExpBank]");
					event.setLine(ID_LINE, ChatColor.DARK_GRAY + "#" + id);
					event.setLine(PLAYER_LINE, ChatColor.GREEN + player.getName());
					event.setLine(3, "0");
					player.sendMessage(String.valueOf(this.prefix) + " created.");
					if (ExpBank.CreateAmt > 0.0) {
						player.sendMessage(String.valueOf(this.prefix) + " You have been charged " + ExpBank.CreateAmt);
					}
				} else {
					player.sendMessage(String.valueOf(this.prefix) + " Not enough " + ExpBank.economy.currencyNamePlural() + " you need "
							+ this.plugin.getConfig().getDouble("economy.create") + " " + ExpBank.economy.currencyNamePlural());
					event.getBlock().breakNaturally();
				}
			} else {
				player.sendMessage(String.valueOf(this.prefix) + " failed to create you dont have permission to do this!");
				event.getBlock().breakNaturally();
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent event) {
		final Block block = event.getClickedBlock();
		final Player player = event.getPlayer();
		if (block != null && (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)) {
			final Sign sign = (Sign) block.getState();
			if (sign.getLine(0).equals(ChatColor.BLUE + "[ExpBank]")) {
				sign.setLine(0, "[ExpBank]");
				sign.setLine(3, sign.getLine(PLAYER_LINE));
				sign.setLine(PLAYER_LINE, "");
				sign.update(true);
				player.sendMessage(String.valueOf(this.prefix) + " Converting...");
			}
			if (sign.getLine(0).equals(ChatColor.stripColor("[ExpBank]")) && Utils.idIsPlayer(sign.getLine(ID_LINE), player)) {
				
				int expInSign = Integer.parseInt(sign.getLine(3));
				
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					if (player.getLevel() >= ExpBank.LevelsDeposit && player.getLevel() > 0) {
						if (expInSign >= ExpBank.LevelsMax) {
							player.sendMessage(String.valueOf(this.prefix) + " I can only hold up to " + ExpBank.LevelsMax + " Levels");
						} else {
							if (ExpBank.DepositAmt != 0.0) {
								Utils.charge(player, ExpBank.DepositAmt);
								if (ExpBank.DepositAmt > 0.0) {
									player.sendMessage(String.valueOf(this.prefix) + " You have been charged " + ExpBank.DepositAmt + " "
											+ ExpBank.economy.currencyNamePlural());
								}
							}
							player.setLevel(player.getLevel() - ExpBank.LevelsDeposit);
							final int samount = expInSign + ExpBank.LevelsDeposit;
							sign.setLine(3, new StringBuilder(String.valueOf(samount)).toString());
							sign.setLine(PLAYER_LINE, Utils.colorForExp(samount) + player.getName());
							sign.update(true);
						}
					} else {
						player.sendMessage(String.valueOf(this.prefix) + " You need atleast " + ExpBank.LevelsDeposit + " Levels");
					}
				} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (expInSign >= ExpBank.LevelsWithdraw) {
						if (ExpBank.WithdrawAmt != 0.0) {
							Utils.charge(player, ExpBank.WithdrawAmt);
							if (ExpBank.WithdrawAmt > 0.0) {
								player.sendMessage(String.valueOf(this.prefix) + " You have been charged " + ExpBank.WithdrawAmt
										+ ExpBank.economy.currencyNamePlural());
							}
						}
						player.setLevel(player.getLevel() + ExpBank.LevelsWithdraw);
						final int samount = expInSign - ExpBank.LevelsWithdraw;
						sign.setLine(3, new StringBuilder(String.valueOf(samount)).toString());
						sign.setLine(PLAYER_LINE, Utils.colorForExp(samount) + player.getName());
						sign.update(true);
					} else {
						player.sendMessage(String.valueOf(this.prefix) + " You need atleast " + ExpBank.LevelsWithdraw + " Levels");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(final BlockBreakEvent event) {
		final Block block = event.getBlock();
		final Player player = event.getPlayer();
		if (block != null) {
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				final Sign sign = (Sign) block.getState();
				if (sign.getLine(0).equalsIgnoreCase(ChatColor.stripColor("[expbank]"))) {
					if (Utils.idIsPlayer(sign.getLine(ID_LINE), player)) {
						Utils.collectLevels(player, sign, Integer.parseInt(sign.getLine(3)), sign.getLine(ID_LINE));
					} else {
						event.setCancelled(true);
						sign.update(true);
						player.sendMessage(String.valueOf(this.prefix) + " Thats not your's!");
					}
				}
			} else {
				BlockFace blockface = BlockFace.NORTH;
				for (int numdirection = 0; numdirection <= 5; ++numdirection) {
					switch (numdirection) {
					case 1:
						blockface = BlockFace.NORTH;
						break;
					case 2:
						blockface = BlockFace.EAST;
						break;
					case 3:
						blockface = BlockFace.SOUTH;
						break;
					case 4:
						blockface = BlockFace.WEST;
						break;
					case 5:
						blockface = BlockFace.UP;
						break;
					}

					Material blockType = block.getRelative(blockface).getType();
					if (blockType == Material.WALL_SIGN || blockType == Material.SIGN_POST) {
						final Sign sign2 = (Sign) block.getRelative(blockface).getState();
						if (sign2.getLine(0).equalsIgnoreCase(ChatColor.stripColor("[expbank]"))) {
							if (Utils.idIsPlayer(sign2.getLine(ID_LINE), player)) {
								Utils.collectLevels(player, sign2, Integer.parseInt(sign2.getLine(3)), sign2.getLine(ID_LINE));
							} else {
								event.setCancelled(true);
								sign2.update(true);
								player.sendMessage(String.valueOf(this.prefix) + " Thats not your's!");
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityExplode(final EntityExplodeEvent event) {
		for (final Block block : event.blockList()) {
			if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
				final Sign sign = (Sign) block.getState();
				if (!sign.getLine(0).equalsIgnoreCase(ChatColor.stripColor("[expbank]"))) {
					continue;
				}
				event.setCancelled(true);
			}
		}
	}
}
