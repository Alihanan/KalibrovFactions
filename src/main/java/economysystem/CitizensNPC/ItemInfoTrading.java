package economysystem.CitizensNPC;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemInfoTrading
{
	final Material ItemType;
	long lastBuyTime;
	double currentPrice;
	final double HyperBasePrice;
	final int maxSize;
	final double HyperBaseStock;
	final long HyperBaseTime;
	final double HyperMarketChange;
	
	/**
	 * Object for price change mechanics
	 * @param initialPrice = current price at the moment of initialization
	 * @param basePrice = base price, should be defined in config
	 * @param baseStock = base stock filling, should be defined in config
	 * @param lastBuy = last time of this item type buying(from this trader)
	 * @param baseTime = base time to sell a stock, should be defined in config
	 * @param type = type of item(Material)
	 * @param marketChange = how strong are market changes, should be defined in config
	 */	
	public ItemInfoTrading(double initialPrice, double basePrice, double baseStock, long lastBuy, long baseTime, Material type,
			double marketChange) {
		currentPrice = initialPrice * 1.0;
		lastBuyTime = lastBuy;
		ItemType = type;
		this.HyperBasePrice = basePrice * 1.0;
		this.HyperBaseStock = baseStock * 1.0;
		maxSize = new ItemStack(ItemType).getMaxStackSize();
		this.HyperBaseTime = baseTime;
		this.HyperMarketChange = marketChange;
	}
	/**
	 * Object for price change mechanics
	 * @param initialPrice = current price at the moment of initialization
	 * @param basePrice = base price, should be defined in config
	 * @param baseStock = base stock filling, should be defined in config
	 * @param lastBuy = last time of this item type buying(from this trader)
	 * @param baseTime = base time to sell a stock, should be defined in config
	 * @param type = type of item(Material)
	 * @param marketChange = how strong are market changes, should be defined in config
	 */	
	public ItemInfoTrading(double initialPrice, double basePrice, double baseStock, long lastBuy, long baseTime, Material type) {
		currentPrice = initialPrice * 1.0;
		lastBuyTime = lastBuy;
		ItemType = type;
		this.HyperBasePrice = basePrice * 1.0;
		this.HyperBaseStock = baseStock * 1.0;
		maxSize = new ItemStack(ItemType).getMaxStackSize();
		this.HyperBaseTime = baseTime;
		this.HyperMarketChange = 0.001;
	}
	
	/**
	 * Should be called every N-th tick. <Optimized externally>
	 */
	public void marketChange(int stock) {
		long currTime = System.currentTimeMillis();
		
		// Current stock to Base stock ratio (in percentage)
		double stockFillPercent = ((stock*1.0) / (1.0 * maxSize)) / HyperBaseStock * 100.0;
		
		// Current price to Base price ratio (in percentage)
		double pricePercent = (currentPrice / HyperBasePrice) * 100.0;
		// Last buy time to Base time ratio  (in percentage)
		double timePercent = ((currTime * 1.0 - lastBuyTime * 1.0) / (HyperBaseTime * 1.0)) * 100.0;
		String s_prev = String.format("Time:(%.2f - %.2f) / %.2f * 100", currTime*1.0, lastBuyTime * 1.0, HyperBaseTime * 1.0);
		System.out.println(s_prev);
		// Total percentage of change
		double changePercent = 100.0 + 0.0; 
		// Start with no change ^^^^^^^^^^
		
		double stockChange = (100.0 - stockFillPercent) * HyperMarketChange;
		changePercent +=  stockChange;
		
		// Bigger than base > tends to decrease = Too many stock not sold
		// Lower than base > tends to increase  = Too low stock -> need to hold
		
		double priceChange = (100.0 - pricePercent) * HyperMarketChange;
		changePercent += priceChange; 
		// Bigger than base > tends to decrease = High price, customer may not buy
		// Lower than base > tends to increase = Low price, income is low
		
		double timeChange = (100.0 - timePercent) * HyperMarketChange;
		changePercent += timeChange;
		s_prev = String.format("TimePrice:(100.0 - %.2f) * %.2f", timePercent, HyperMarketChange);
		System.out.println(s_prev);
		// Bigger than base > tends to decrease = Too long waiting, need to sell!
		// Lower than base > tends to increase = Too short waiting, it is being bought!
		
		
		// LastBuyTime + maxStackSize + basePrice + ...
		
		currentPrice = changePriceByPercent(currentPrice, changePercent);
		String s = String.format("Price:%.2f, Market:%.2f, stockChange:%.2f, priceChange:%.2f, timeChange:%.2f", 
				currentPrice, changePercent, stockChange, priceChange, timeChange);
		System.out.println(s);
	}
	/**
	 * Should be called after item bought from player(- money, +stock)
	 */
	public void itemBought(int stock) {
		marketChange(stock);	
	}
	/**
	 * Should be called after item sold to player(+ money, - stock)
	 */
	public void itemSold(int stock) {
		setNewBuyTime();
		marketChange(stock);	
	}
	
	private double changePriceByPercent(double price, double percent) {
		double newPrice = price * percent / 100.0;
		return newPrice;
	}
	
	/**
	 * Get price from item info
	 * @return 
	 */
	public int getPrice(int amount) {
		int pr = (int)(currentPrice * (1.0 * amount));
		if(pr == 0) pr = 1;
		return pr;
	}
	
	/**
	 * Should be called after item buy
	 */
	public void setNewBuyTime() {
		lastBuyTime = System.currentTimeMillis();
	}
	
}