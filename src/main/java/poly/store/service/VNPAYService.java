package poly.store.service;

import javax.servlet.http.HttpServletRequest;

public interface VNPAYService {
	  String payment(HttpServletRequest request, int amount, String orderInfor, String urlReturn);
	  int paymentReturn(HttpServletRequest request);
	
}
