package serverSide;

import org.json.simple.JSONObject;

public class OrderManager {

	private BankService bank = new BankService();
	private DataBase db;

	public OrderManager(DataBase db) {
		this.db = db;
	}

//	public JSONObject makeOrder(JSONObject order) {

//		int orderId = db.addOrder(order);
//
//		if (orderId == -1) {
//			return null; // TODO decide what to return if adding the order failed
//		} else {
//			JSONObject json = new JSONObject();
//			Payment p = new Payment((double) order.get("payment"));
//
//			if (bank.handlePayment(p)) {
//				json.put("orderId", orderId);
//
//				EventManager.getInstance().notify("order created", json);
//				json.clear();
//				json.put("result", "succeed");
//				return json;
//			} else {
//				json.put("result", "failed");
//				return json;
//			}
//		}
//	}
	
	public void updateOrder() {
		
	}
}
