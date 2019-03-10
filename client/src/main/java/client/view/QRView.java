package client.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import client.Launch;
import client.view.function.Wxbot;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * 微信登录二维码登录窗口
 * 
 * @author wr
 *
 */
public class QRView extends AnchorPane {

	public Stage QR_STAGE;
	private Wxbot wxbot;
	private ProgressIndicator pi;
	private Parent root;
	private Label label;
	private ImageView qr_img;

	// imgview 点击事件标识
	private Boolean eventFlag = false;

	public QRView() {
		QR_STAGE = new Stage();
		// 获取机器人实例
		wxbot = Launch.context.getBean(Wxbot.class);
	}
	
	public void close() {
		QR_STAGE.hide();
	}

	public void open(byte[] qrData) throws IOException {
		if (QR_STAGE.isShowing()) {
			root = QR_STAGE.getScene().getRoot();
			pi.setProgress(0.6f);

			qr_img = ((ImageView) root.lookup("#qrCodeImg"));
			InputStream is = new ByteArrayInputStream(qrData);
			qr_img.setImage(new Image(is));

			pi.setProgress(1f);
			pi.setVisible(false);

			qr_img.setVisible(true);
			// 更改lable 显示文字
			label = (Label) root.lookup("#msgLab");
			label.setText("请使用微信扫一扫以登录");
		} else {
			// 隐藏登录窗口
//			LoginController.LOGIN_STAGE.hide();
			LoginView.getInstance().hide();

			root = FXMLLoader.load(getClass().getClassLoader().getResource("QR.fxml"));
			// 获取ImageView 并赋值
			qr_img = ((ImageView) root.lookup("#qrCodeImg"));
			InputStream is = new ByteArrayInputStream(qrData);
			qr_img.setImage(new Image(is));

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
			QR_STAGE.setIconified(false);
			QR_STAGE.setScene(scene);
			QR_STAGE.setTitle("微信登录");
			QR_STAGE.setResizable(false);
			QR_STAGE.setOnCloseRequest(e -> {
				QR_STAGE.close();
				LoginView.getInstance().close();
			});
			QR_STAGE.show();
		}
	}

	public void expired() {
		// 刷新二维码 要重启机器人程序
		wxbot.stop();
		eventFlag = true;
		// 更换图片
		Image mask = new Image(getClass().getClassLoader().getResource("reload.jpg").toString());
		qr_img = ((ImageView) QR_STAGE.getScene().getRoot().lookup("#qrCodeImg"));
		qr_img.setImage(mask);
		qr_img.setOnMouseClicked(e -> {
			if (eventFlag) {
				qr_img.setVisible(false);
				pi = (ProgressIndicator) QR_STAGE.getScene().lookup("#progressForQR");
				pi.setVisible(true);
				pi.setProgress(0.3f);

				wxbot.start(null);
				eventFlag = false;
			}
		});

		// 更改lable 显示文字
		label = (Label) QR_STAGE.getScene().getRoot().lookup("#msgLab");
		label.setText("二维码已失效，点击刷新");
	}
}
