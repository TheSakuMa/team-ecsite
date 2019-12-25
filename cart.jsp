<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>カート画面</title>
		<link rel="stylesheet" href="./css/spring.css">
		<link rel="stylesheet" href="./css/header.css">
		<link rel="stylesheet" href="./css/page-title.css">
		<link rel="stylesheet" href="./css/submit-btn.css">
		<link rel="stylesheet" href="./css/cart.css">
		<link rel="stylesheet" href="./css/vertical-table.css">
		<link rel="stylesheet" href="./css/message.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
		<script type="text/javascript" src="./js/setAction.js"></script>
	</head>
	<body>
		<jsp:include page="header.jsp"/>
		<h1>カート</h1>
		<div id="main">
		<s:if test="emptyCartMessage != null">
			<div class="info">
				<s:property value="emptyCartMessage"/>
			</div>
		</s:if>
		<s:else>
			<s:form id="form">
				<table border="1" class="vertical-table">
					<tr>
						<th>＃</th>
						<th>商品名</th>
						<th>商品名ふりがな</th>
						<th>商品画像</th>
						<th>値段</th>
						<th>発売会社名</th>
						<th>発売年月日</th>
						<th>購入個数</th>
						<th>合計金額</th>
					</tr>
				<s:iterator value="productInfoListinCart">
					<tr>
						<td><input type="checkbox" name="productIdList" value="<s:property value='productId'/>"/></td>
						<td><s:property value="productName"/></td>
						<td><s:property value="productNameKana"/></td>
						<td><img src="./<s:property value='imageFilePath'/>/<s:property value='imageFileName'/>"></td>
						<td><s:property value="price"/>円</td>
						<td><s:property value="releaseCompany"/></td>
						<td><s:property value="releaseDate"/></td>
						<td><s:property value="productCount"/></td>
						<td><s:property value="productCount * price"/>円</td>
					</tr>
				</s:iterator>
				</table>
				<div class="cart-total-price">
					<span>カート合計金額：</span><s:property value="cartTotalPrice"/>円
				</div>
				<div class="submit-btn-box">
				<s:if test="#session.loginFlg == 1">
					<input type="button" id="buyCartItem" onclick="setAction('SettlementConfirmAction')" class="submit-btn" value="決済">
				</s:if>
				<s:else>
					<s:hidden name="cartFlg" value="1"/>
						<input type="button" id="buyCartItem" onclick="setAction('GoLoginAction')" class="submit-btn" value="決済">
				</s:else>
					<input type="button" id="deleteCartItem" onclick="setAction('DeleteCartAction')" class="submit-btn empty-cart" value="削除" disabled="disabled">
				</div>
			</s:form>
			</s:else>
		</div>
		<script type="text/javascript">
		(function() {
			$('input[name="productIdList"]').change(function() {
				if($('input[name="productIdList"]').is(':checked')) {
					// 選択されている場合、削除ボタンを有効化]
					$('#deleteCartItem').prop('disabled', false);
				} else {
					// 選択されていない場合、削除ボタンを無効化
					$('#deleteCartItem').prop('disabled', true);
				}
			});
		}());
		</script>
	</body>
</html>