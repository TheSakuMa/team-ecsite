package com.internousdev.spring.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.spring.dao.CartInfoDAO;
import com.internousdev.spring.dto.CartInfoDTO;
import com.opensymphony.xwork2.ActionSupport;

public class DeleteCartAction extends ActionSupport implements SessionAware {
	private Map<String, Object> session;
	private String[] productIdList;
	private CartInfoDAO cartInfoDAO = new CartInfoDAO();
	private List<CartInfoDTO> productInfoListinCart = new ArrayList<CartInfoDTO>();
	private String emptyCartMessage = null;
	private int cartTotalPrice = 0;

	public String execute() throws SQLException {
		String result = ERROR;

		String userId = null;

		String tempLogined = String.valueOf(session.get("loginFlg"));
		int loginFlg = "null".equals(tempLogined)? 0 : Integer.parseInt(tempLogined);
		if (loginFlg == 1) {
			userId = session.get("userId").toString();
		} else {
			userId = String.valueOf(session.get("tempUserId"));
		}

		int deleteResult = 0;

		for (String productId: productIdList) {
			deleteResult += cartInfoDAO.deleteCartItem(userId, Integer.parseInt(productId));
		}
		// 削除に成功した件数と、削除対象としてjspから送られてきた件数が一致するかを確認
		if (deleteResult == productIdList.length) {
			productInfoListinCart = cartInfoDAO.getProductInfoinCart(userId);
			if(productInfoListinCart.size() > 0) {
				// カート情報が存在した場合、カート画面にカート情報を表示させるための処理
				result = SUCCESS;
				for (CartInfoDTO s: productInfoListinCart) {
					cartTotalPrice += s.getPrice() * s.getProductCount();
				}
			} else {
				// カート情報が存在しない場合
				result = SUCCESS;
				setEmptyCartMessage("カート情報がありません。");
			}
		}
		return result;
	}

	public void setProductIdList(String[] productIdList) {
		this.productIdList = productIdList;
	}

	public List<CartInfoDTO> getProductInfoListinCart() {
		return productInfoListinCart;
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
