package com.pera_software;

import java.nio.file.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import com.pera_software.aidkit.visualstudio.*;

public class Main extends Application {

	public static void main( String arguments[] ) {
		Settings.parseCommandLine( arguments );
		launch( arguments );
	}

	@Override
	public void start( Stage stage ) throws Exception {
		StackPane stackPane = new StackPane();

		for ( String solutionFileName : Settings.solutionFileNames() ) {
			Path solutionFilePath = Paths.get( solutionFileName );
			SolutionFile solutionFile = new SolutionFile( solutionFilePath );
			TreeItem< String > solutionItem = new TreeItem<>( solutionFilePath.getFileName().toString() );
			for ( ProjectFile projectFile : solutionFile.findProjects() ) {
				buildProjectTree( solutionItem, projectFile );
			}
			solutionItem.setExpanded( true );
			TreeView< String > treeView = new TreeView<>( solutionItem );
			stackPane.getChildren().add( treeView );
		}
		Scene scene = new Scene( stackPane );
		stage.setScene( scene );
		stage.show();
	}

	private static void buildProjectTree( TreeItem< String > parentItem , ProjectFile projectFile  ) throws Exception {
		TreeItem< String > projectItem = new TreeItem<>( projectFile.path().getFileName().toString() );
		parentItem.getChildren().add( projectItem );

		for ( ProjectFile projectReference : projectFile.getProjectReferences( ) )
			buildProjectTree( projectItem, projectReference );
	}
}
