package com.pera_software;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Main extends Application {

	public static void main( String arguments[] ) {
		launch( arguments );
	}

	@Override
	public void start( Stage stage ) throws Exception {
		TreeItem< String > root = new TreeItem<>( "CMWMarsAndLoggingServer" );
		root.setExpanded( true );
		
		TreeItem< String > loggingServerItem = new TreeItem<>( "LoggingServer" );
		loggingServerItem.getChildren().add( new TreeItem<>( "Boost" ));
		
		root.getChildren().add( loggingServerItem );
		root.getChildren().add( new TreeItem<>( "LoggingServerCUI" ));
		
		TreeView< String > tree = new TreeView<>( root );
		
		StackPane pane = new StackPane();
		pane.getChildren().add( tree );
		
		Scene scene = new Scene( pane );
		stage.setScene( scene );
		stage.show();
	}
}
