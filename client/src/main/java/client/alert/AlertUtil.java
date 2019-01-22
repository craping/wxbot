package client.alert;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class AlertUtil {
	/**
	 * 确认窗口（Confirmation）
	 * 
	 * @param header  对话框的信息标题
	 * @param message 对话框的信息
	 * @return 用户点击了是或否
	 */
	public static boolean confirmDialog(Stage stage, String header, String message) {

		Alert _alert = new Alert(Alert.AlertType.CONFIRMATION, message, new ButtonType("取消", ButtonBar.ButtonData.NO),
				new ButtonType("确定", ButtonBar.ButtonData.YES));
		_alert.setTitle("确认");
		_alert.setHeaderText(header);
		_alert.initOwner(stage);
		Optional<ButtonType> _buttonType = _alert.showAndWait();
		if (_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 消息窗口（Information）
	 * 
	 * @param stage   当前舞台
	 * @param header  对话框的信息标题
	 * @param message 对话框的信息
	 * @return 用户点击了是或否
	 */
	public static void informationDialog(Stage stage, String header, String message) {
		Alert _alert = new Alert(Alert.AlertType.INFORMATION);
		_alert.setTitle("信息");
		_alert.setHeaderText(header);
		_alert.setContentText(message);
		_alert.initOwner(stage);
		_alert.show();
	}

	/**
	 * 错误窗口（Error）
	 * 
	 * @param stage   当前舞台
	 * @param header  对话框的信息标题
	 * @param message 对话框的信息
	 * @return 用户点击了是或否
	 */
	public static void errorDialog(Stage stage, String header, String message) {
		Alert _alert = new Alert(Alert.AlertType.ERROR);
		_alert.setTitle("错误");
		_alert.setHeaderText(header);
		_alert.setContentText(message);
		_alert.initOwner(stage);
		_alert.show();
	}
}
