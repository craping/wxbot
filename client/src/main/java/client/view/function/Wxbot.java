package client.view.function;

import client.view.AppView;
import javafx.application.Platform;
import netscape.javascript.JSObject;

public class Wxbot {
	
	public void exit() {
        Platform.exit();
    }
	
	public void param(String name, JSObject json) {
		System.out.println(name);
		System.out.println(json.getMember("a"));
	}
	
	public void excute() {
		AppView.executeScript("alert(1);");
	}
}
