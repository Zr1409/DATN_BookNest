package poly.store.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import poly.store.dao.AddressDao;
import poly.store.dao.UserDao;
import poly.store.entity.Address;
import poly.store.entity.District;
import poly.store.entity.Province;
import poly.store.entity.User;
import poly.store.entity.Ward;
import poly.store.model.AddressModel;
import poly.store.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {
	@Autowired
	private AddressDao addressDao;

	@Autowired
	private UserDao userDao;

	private static final String BASE_URL = "https://online-gateway.ghn.vn/shiip/public-api/master-data";
	private static final String TOKEN = "17c8d9c0-04c1-11f0-aff4-822fc4284d92";
	private final RestTemplate restTemplate = new RestTemplate();

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Token", TOKEN);
		return headers;
	}

	@Override
	public List<Address> findListAddressByEmail(String username) {
		return addressDao.findListAddressByEmail(username);
	}

	@Override
	public List<Province> findAllProvince() {
		String url = BASE_URL + "/province";
		HttpEntity<String> entity = new HttpEntity<>(createHeaders());
		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");
		List<Province> provinces = new ArrayList<>();
		for (Map<String, Object> item : data) {
			Province province = new Province();
			province.setId((Integer) item.get("ProvinceID"));
			province.setName((String) item.get("ProvinceName"));
			provinces.add(province);
		}
		return provinces;
	}

	@Override
	public AddressModel createAddress(AddressModel addressModel) {
		User user = getCurrentUser();
		Address address = new Address();
		address.setFullname(addressModel.getFullName());
		address.setPhone(addressModel.getPhone());
		address.setDetail(addressModel.getDetail());
		address.setProvince(addressModel.getProvince());
		address.setDistrict(addressModel.getDistrict());
		address.setWard(addressModel.getWard());
		address.setUser(user);
		addressDao.save(address);
		return addressModel;
	}

	@Override
	public Address getAddressById(int id) {
		return addressDao.findById(id).orElse(null);
	}

	@Override
	public void delete(Address address) {
		addressDao.delete(address);
	}

	@Override
	public Address findAddressById(String username, int id) {
		return addressDao.findAddressById(username, id);
	}

	@Override
	public AddressModel getOneAddressById(int id) {
		Address address = getAddressById(id);
		if (address == null)
			return null;
		AddressModel addressModel = new AddressModel();
		addressModel.setFullName(address.getFullname());
		addressModel.setPhone(address.getPhone());
		addressModel.setDetail(address.getDetail());
		addressModel.setProvince(address.getProvince());
		addressModel.setDistrict(address.getDistrict());
		addressModel.setWard(address.getWard());
		return addressModel;
	}

	@Override
	public AddressModel updateAddress(AddressModel addressModel, Integer id) {
		Address address = getAddressById(id);
		if (address == null)
			return null;
		address.setFullname(addressModel.getFullName());
		address.setPhone(addressModel.getPhone());
		address.setDetail(addressModel.getDetail());
		address.setProvince(addressModel.getProvince());
		address.setDistrict(addressModel.getDistrict());
		address.setWard(addressModel.getWard());
		addressDao.save(address);
		return addressModel;
	}

	private User getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;

		// Kiểm tra xem principal có phải là UserDetails hay không
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else if (principal instanceof OAuth2User) {
			// Nếu là OAuth2User (DefaultOAuth2User), lấy username (thường là email)
			OAuth2User oauth2User = (OAuth2User) principal;
			username = (String) oauth2User.getAttribute("email"); // Hoặc sử dụng attribute khác nếu có
		}

		if (username == null) {
			throw new RuntimeException("User not authenticated");
		}
		return userDao.findUserByEmail(username);
	}

	@Override
	public List<District> findDistrictByIdProvince(Integer id) {
		String url = BASE_URL + "/district?province_id=" + id;
		HttpEntity<String> entity = new HttpEntity<>(createHeaders());
		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

		List<District> districts = new ArrayList<>();
		for (Map<String, Object> item : data) {
			District district = new District();
			district.setId((Integer) item.get("DistrictID"));
			district.setName((String) item.get("DistrictName"));
			districts.add(district);
		}
		return districts;
	}

	@Override
	public List<Ward> findWardByIdProvinceAndIdDistrict(Integer idProvince, Integer idDistrict) {
		String url = BASE_URL + "/ward?district_id=" + idDistrict;
		HttpEntity<String> entity = new HttpEntity<>(createHeaders());
		ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
		List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");

		List<Ward> wards = new ArrayList<>();
		for (Map<String, Object> item : data) {
			Ward ward = new Ward();
			ward.setId((String) item.get("WardCode"));
			ward.setName((String) item.get("WardName"));
			wards.add(ward);
		}
		return wards;
	}

	@Override
	public List<District> getListDistrictByAdressId(Integer id) {
		Address address = addressDao.findById(id).orElse(null);
		if (address == null || address.getProvince() == null) {
			return new ArrayList<>();
		}

		// Lấy ID của tỉnh từ tên
		Integer provinceId = getProvinceIdByName(address.getProvince());
		if (provinceId == null) {
			return new ArrayList<>();
		}

		return findDistrictByIdProvince(provinceId);
	}

	@Override
	public List<Ward> getListWardByAdressId(Integer id) {
		Address address = addressDao.findById(id).orElse(null);
		if (address == null || address.getProvince() == null || address.getDistrict() == null) {
			return new ArrayList<>();
		}

		// Lấy ID của tỉnh từ tên
		Integer provinceId = getProvinceIdByName(address.getProvince());
		if (provinceId == null) {
			return new ArrayList<>();
		}

		// Lấy ID của quận/huyện từ tên
		Integer districtId = getDistrictIdByName(address.getDistrict(), provinceId);
		if (districtId == null) {
			return new ArrayList<>();
		}

		return findWardByIdProvinceAndIdDistrict(provinceId, districtId);
	}

	private Integer getProvinceIdByName(String provinceName) {
		List<Province> provinces = findAllProvince(); // Gọi API GHN để lấy danh sách tỉnh
		for (Province province : provinces) {
			if (province.getName().equalsIgnoreCase(provinceName)) {
				return province.getId();
			}
		}
		return null; // Nếu không tìm thấy
	}

	private Integer getDistrictIdByName(String districtName, Integer provinceId) {
		List<District> districts = findDistrictByIdProvince(provinceId); // Gọi API GHN để lấy danh sách quận
		for (District district : districts) {
			if (district.getName().equalsIgnoreCase(districtName)) {
				return district.getId();
			}
		}
		return null; // Nếu không tìm thấy
	}

}
