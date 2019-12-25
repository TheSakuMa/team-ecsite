package com.internousdev.spring.action;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.spring.dao.CartInfoDAO;
import com.internousdev.spring.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class CartAction extends ActionSupport implements SessionAware {
	private Map<String, Object> session;
	private CartInfoDAO cartInfoDAO = new CartInfoDAO();
	private String emptyCartMessage = null;
	List<CartInfoDTO> productInfoListinCart = new ArrayList<CartInfoDTO>();
	private int cartTotalPrice = 0;

	public String execute() throws SQLException {
		String result = ERROR;

		// 仮ユーザーID及びユーザーIDが両方存在しない場合に、セッションタイムアウト処理を行う
		if (!session.containsKey("tempUserId") && !session.containsKey("userId")) {
			return "session Timeout";
		}

		String userId = null;

		String tempLogined = String.valueOf(session.get("loginFlg"));
		int loginFlg = "null".equals(tempLogined)? 0 : Integer.parseInt(tempLogined);
		if (loginFlg == 1) {
			userId = session.get("userId").toString();
		} else {
			userId = String.valueOf(session.get("tempUserId"));
		}

		productInfoListinCart = cartInfoDAO.getProductInfoinCart(userId);
		// カート情報が存在するかどうかチェック
		if (productInfoListinCart.size() > 0) {
			// カート情報が存在した場合
			result = SUCCESS;
			for (CartInfoDTO s: productInfoListinCart) {
				cartTotalPrice += s.getPrice() * s.getProductCount();
			}
		} else {
			// カート情報が存在しない場合
			result = SUCCESS;
			setEmptyCartMessage("カート情報がありません。");
		}
		return result;
	}

	public List<CartInfoDTO> getProductInfoListinCart() {
		return productInfoListinCart;
	}
	public void setProductInfoListinCart(List<CartInfoDTO> productInfoListinCart) {
		this.productInfoListinCart = productInfoListinCart;
	}

	public String getEmptyCartMessage() {
		return emptyCartMessage;
	}
	public void setEmptyCartMessage(String emptyCartMessage) {
		this.emptyCartMessage = emptyCartMessage;
	}

	public int getCartTotalPrice() {
		return cartTotalPrice;
	}

	public Map<String, Object> getSession() {
		return session;
	}
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
