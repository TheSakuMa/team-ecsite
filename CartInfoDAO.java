package com.internousdev.spring.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.internousdev.spring.dto.CartInfoDTO;
import com.internousdev.spring.util.DBConnector;

public class CartInfoDAO {

	/**
	 * 1. 「カートに追加ボタン押下」時に、カート情報テーブル内でユーザー（ログイン時：ユーザーID、未ログイン時：仮ユーザーID）
	 * に紐づくカート情報に、追加しようとしている商品IDと一致するデータが存在するかをチェックする。
	 * 2. ログイン認証機能で使用。 「カート情報の紐付け 2)」の、
	 * カート情報テーブル内で「ユーザーID」に紐づく同じ商品IDのカート情報が存在するかのチェックを行うための処理。
	 * @param userId
	 * @param productId
	 * @return
	 * @throws SQLException
	 */
	public boolean isCartInfoExistsByUserIdandProductId(String userId, int productId) throws SQLException {
		boolean result = false;
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		String sql = "SELECT COUNT(*) AS count from cart_info WHERE user_id = ? AND product_id = ?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt("count") > 0) {
					result = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return result;
	}

	/**
	 * 「カートに追加ボタン押下」時、ユーザーに紐づくカート情報に、追加する商品IDと一致するデータが存在する場合に、
	 * 個数を足した値で更新する処理。
	 * @param userId
	 * @param productId
	 * @param productCount
	 * @return int型で、更新に成功した件数
	 */
	public int addtoCart(String userId, int productId, int productCount) throws SQLException {
		int result = 0;
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		String sql = "UPDATE cart_info SET product_count = product_count + ?, update_date = now() WHERE user_id = ? AND product_id = ?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, productCount);
			ps.setString(2, userId);
			ps.setInt(3, productId);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return result;
	}

	/**
	 * 「カートに追加ボタン押下」時、ユーザーに紐づくカート情報に、追加する商品IDと一致するデータが存在しない場合に、
	 * カート情報を登録する処理。
	 * @param userId
	 * @param productId
	 * @return int型で、カート情報テーブルへの登録に成功した件数
	 */
	public int initialAddtoCart(String userId, int productId, int productCount) throws SQLException {
		int result = 0;
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		String sql = "INSERT INTO cart_info (user_id, product_id, product_count, regist_date, update_date) VALUES (?,?,?,now(),now())";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			ps.setInt(3, productCount);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return result;
	}

	/**
	 * 1. カート機能で使用→
	 * ユーザーID（もしくは仮ユーザーID）と商品IDを元にカート情報テーブルから情報を取得。
	 * また、該当する商品の商品情報を商品情報テーブルから取得。
	 * 2. 決済確認画面「決済ボタン押下」で、購入対象であるカート情報を取得するために使用。
	 * 仮ユーザーIDもしくはユーザーIDに紐づいたカートテーブル情報を取得。
	 * @param productionId
	 * @param userId
	 * @return ユーザーID（もしくは仮ユーザーID）と商品IDに紐づいた商品情報テーブルと購入個数（List<CartInfoDTO>型）
	 * @throws SQLException
	 */
	public List<CartInfoDTO> getProductInfoinCart(String userId) throws SQLException {
		List<CartInfoDTO> cartInfoList = new ArrayList<CartInfoDTO>();
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		String sql = "SELECT pit.product_id, pit.product_name, pit.product_name_kana, pit.price, pit.image_file_path, pit.image_file_name, pit.release_date, pit.release_company, cart_info.product_count "
				+ "FROM product_info pit INNER JOIN cart_info "
				+ "ON pit.product_id = cart_info.product_id "
				+ "WHERE cart_info.user_id = ? AND pit.status = 1 ORDER BY cart_info.update_date DESC, cart_info.regist_date DESC";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				CartInfoDTO cartInfoDTO = new CartInfoDTO();
				cartInfoDTO.setProductId(rs.getInt("product_id"));
				cartInfoDTO.setProductCount(rs.getInt("product_count"));
				cartInfoDTO.setProductName(rs.getString("product_name"));
				cartInfoDTO.setProductNameKana(rs.getString("product_name_kana"));
				cartInfoDTO.setPrice(rs.getInt("price"));
				cartInfoDTO.setImageFilePath(rs.getString("image_file_path"));
				cartInfoDTO.setImageFileName(rs.getString("image_file_name"));
				cartInfoDTO.setReleaseDate(rs.getDate("release_date"));
				cartInfoDTO.setReleaseCompany(rs.getString("release_company"));
				cartInfoList.add(cartInfoDTO);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return cartInfoList;

	}

	/**
	 * カート機能の「削除」で使用
	 * @param userId
	 * @param productId
	 * @return int型で削除に成功した件数。
	 */
	public int deleteCartItem(String userId, int productId) throws SQLException {
		int result = 0;
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		String sql = "DELETE FROM cart_info WHERE user_id = ? AND product_id = ?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setInt(2, productId);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return result;
	}

	/**
	 * 1. ログイン認証機能の「カート情報の紐付け」で使用。
	 * 1-1. カート情報テーブル内で「仮ユーザーID」に紐づくカート情報存在しているかの確認に使用。
	 * 1-2. カート情報テーブル内で「ユーザーID」に紐づく同じ商品IDのカート情報が存在する場合に、
	 * 個数の更新とその後の処理を行うための商品IDおよび個数を利用するために使用。
	 * @param userId
	 * @return
	 */
	public List<CartInfoDTO> getCartInfo(String userId) throws SQLException {
		List<CartInfoDTO> cartInfoList = new ArrayList<CartInfoDTO>();
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		String sql = "SELECT * FROM cart_info WHERE user_id = ?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				CartInfoDTO cartInfoDTO = new CartInfoDTO();
				cartInfoDTO.setId(rs.getInt("id"));
				cartInfoDTO.setProductId(rs.getInt("product_id"));
				cartInfoDTO.setProductCount(rs.getInt("product_count"));
//				cartInfoDTO.setRegistDate(rs.getString("regist_date"));
//				cartInfoDTO.setUpdateDate(rs.getString("update_date"));
				cartInfoList.add(cartInfoDTO);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return cartInfoList;
	}

	/**
	 * ログイン認証機能で使用。
	 * カート情報テーブル内で「ユーザーID」に紐づく同じ商品IDのカート情報が存在する場合、
	 * 個数を足した値で更新する。その後、処理対象のカート情報を削除する。
	 * @param userId
	 * @param tempUserId
	 * @param productCount
	 * @return
	 * @throws SQLException
	 */
	public int addCartByLogin(String userId, String tempUserId,  int productId, int productCount) throws SQLException {
		int result = 0;
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		String update = "UPDATE cart_info SET product_count = product_count + ?, update_date = now() WHERE user_id = ? AND product_id = ?";

		try {
			PreparedStatement ps = con.prepareStatement(update);
			ps.setInt(1, productCount);
			ps.setString(2, userId);
			ps.setInt(3, productId);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return result;
	}

	/**
	 * ログイン認証機能で使用。
	 * 「カート情報の紐付け 2)」のカート情報テーブル内で「ユーザーID」に紐づく同じ商品IDのカート情報が存在しない場合、
	 * 処理対象のカート情報のユーザーID(仮ユーザーID)をログインするユーザーIDに更新する。
	 * @param tempUserId
	 * @param userId
	 * @param productId
	 * @return
	 * @throws SQLException
	 */
	public int updateCartUserId(String userId, String tempUserId, int productId) throws SQLException {
		int result = 0;
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();

		String sql = "UPDATE cart_info SET user_id = ?, update_date = now() WHERE user_id = ? AND product_id = ?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, tempUserId);
			ps.setInt(3, productId);
			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return result;
	}

	/**
	 * 決済確認画面の「決済ボタン押下」で、ユーザーに紐づいているカート情報を削除する
	 * @param userId
	 * @return int型 削除に成功したレコード件数
	 */
	public boolean deleteCartItemBuySuccess(String userId) throws SQLException {
		DBConnector db = new DBConnector();
		Connection con = db.getConnection();
		boolean result = false;
		int res  = 0;

		String sql = "DELETE FROM cart_info WHERE user_id = ?";

		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userId);
			res = ps.executeUpdate();
			if (res > 0) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return result;
	}
}
