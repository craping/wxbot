package client.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import client.controller.LoginController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class QRView extends AnchorPane {

	public Stage QR_STAGE;

	public QRView() {
		QR_STAGE = new Stage();
	}

	public void open(byte[] qrData) throws IOException {

		LoginController.LOGIN_STAGE.hide();
		ImageView qr_img = new ImageView();
		InputStream is = new ByteArrayInputStream(qrData);
		qr_img.setImage(new Image(is));
		getChildren().add(qr_img);

		Scene scene = new Scene(this, 440, 440);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
		QR_STAGE.setIconified(false);
		QR_STAGE.setScene(scene);
		QR_STAGE.setTitle("微信登录二维码");
		QR_STAGE.setResizable(false);
		QR_STAGE.setOnCloseRequest(e -> {
			QR_STAGE.close();
			System.exit(0);
		});
		QR_STAGE.show();
	}
}
