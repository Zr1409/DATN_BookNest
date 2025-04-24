package poly.store.service;

public interface ShippingService {
	Double getShippingFee(int toDistrictId, String toWardCode, int weight);

}
