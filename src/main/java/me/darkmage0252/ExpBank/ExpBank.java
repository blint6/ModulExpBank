package me.darkmage0252.ExpBank;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.Listener;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

public class ExpBank extends JavaPlugin
{
    static boolean PermsEnabled;
    static boolean EconomyEnabled;
    public static Economy economy;
    public static Permission permission;
    public static double CreateAmt;
    public static double ReturnAmt;
    public static double DepositAmt;
    public static double WithdrawAmt;
    public static int LevelsMax;
    public static int LevelsWithdraw;
    public static int LevelsDeposit;
    public static long maxId;
    
    static {
        ExpBank.PermsEnabled = false;
        ExpBank.EconomyEnabled = false;
        ExpBank.economy = null;
        ExpBank.permission = null;
        ExpBank.CreateAmt = 0.0;
        ExpBank.ReturnAmt = 0.0;
        ExpBank.DepositAmt = 0.0;
        ExpBank.WithdrawAmt = 0.0;
        ExpBank.LevelsMax = 25;
        ExpBank.LevelsWithdraw = 1;
        ExpBank.LevelsDeposit = 1;
    }
    
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.setupPermissions();
        this.setupEconomy();
        ExpBank.LevelsMax = this.getConfig().getInt("levels.max");
        ExpBank.LevelsWithdraw = this.getConfig().getInt("levels.withdraw");
        ExpBank.LevelsDeposit = this.getConfig().getInt("levels.deposit");
        ExpBank.CreateAmt = this.getConfig().getDouble("economy.create");
        ExpBank.ReturnAmt = this.getConfig().getDouble("economy.return");
        ExpBank.DepositAmt = this.getConfig().getDouble("economy.deposit");
        ExpBank.WithdrawAmt = this.getConfig().getDouble("economy.withdraw");
        ExpBank.maxId = this.getConfig().getLong("ids.max", 1);
        Utils.plugin = this;
        this.getServer().getPluginManager().registerEvents((Listener)new ExpBankListener(this), (Plugin)this);
    }
    
    public void onDisable() {
    }
    
    public boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            System.out.println("Economy not found " + ExpBank.EconomyEnabled);
            return false;
        }
        if (this.getConfig().getBoolean("economy.enable")) {
            final RegisteredServiceProvider<Economy> economyProvider = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
            if (economyProvider != null) {
                System.out.println("Economy enabled");
                ExpBank.economy = (Economy)economyProvider.getProvider();
                ExpBank.EconomyEnabled = true;
            }
            return ExpBank.economy != null;
        }
        return false;
    }
    
    private boolean setupPermissions() {
        if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
            System.out.println("permissions found");
            final RegisteredServiceProvider<Permission> permissionProvider = (RegisteredServiceProvider<Permission>)this.getServer().getServicesManager().getRegistration((Class)Permission.class);
            if (permissionProvider != null) {
                ExpBank.permission = (Permission)permissionProvider.getProvider();
                ExpBank.PermsEnabled = true;
            }
            return ExpBank.permission != null;
        }
        System.out.println("Vault not found using op " + ExpBank.PermsEnabled);
        return false;
    }
}
