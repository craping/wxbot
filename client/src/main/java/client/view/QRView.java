package client.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class QRView extends AnchorPane {
	
	public QRView () {}

	public static Stage QR_STAGE;
	
	public void open() throws IOException {
		QR_STAGE = new Stage();
		Scene scene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("qr.fxml")));
		scene.getStylesheets().add(getClass().getClassLoader().getResource("application.css").toExternalForm());
		QR_STAGE.setIconified(false);
		QR_STAGE.setScene(scene);
		QR_STAGE.setTitle("微信登录二维码");
		QR_STAGE.setOnCloseRequest(e -> {
			System.exit(0);
		});
		QR_STAGE.show();
	}
}
