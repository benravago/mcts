package app.Reversi.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class App extends Application {

  @Override
  public void start(Stage stage) {

    var model = new Board();
    var view = new View();
    new Controller(model, view);

    var scene = new Scene(view.pane);
    scene.getStylesheets().add(resource("style.css"));
    stage.setScene(scene);
    stage.show();
  }

  String resource(String name) {
    return getClass().getResource(name).toExternalForm();
  }
}
