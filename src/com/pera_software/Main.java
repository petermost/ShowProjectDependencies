package com.pera_software;

import java.nio.file.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import com.pera_software.aidkit.visualstudio.*;

//##################################################################################################

public class Main extends Application {

	private static final String SLN_ICON_NAME = "resources/icons16x16/visual-studio-solution.png";
	private static final String CPP_ICON_NAME = "resources/icons16x16/text-x-c++src.png";
	private static final String CS_ICON_NAME  = "resources/icons16x16/text-x-csharp.png";
	private static final String LIB_ICON_NAME = "resources/icons16x16/shared-lib.png";
	
	//==============================================================================================
	
	public static void main( String arguments[] ) {
		Settings.parseCommandLine( arguments );
		launch( arguments );
	}

	//==============================================================================================
	
	@Override
	public void start( Stage stage ) throws Exception {
		StackPane stackPane = new StackPane();

		for ( String solutionFileName : Settings.solutionFileNames() ) {
			SolutionFile solutionFile = new SolutionFile( Paths.get( solutionFileName ));
			TreeItem< String > solutionItem = createSolutionItem( solutionFile );
			TreeView< String > treeView = new TreeView<>( solutionItem );
			
			for ( ProjectFile projectFile : solutionFile.findProjects() ) {
				buildProjectTree( solutionItem, projectFile );
			}
			stackPane.getChildren().add( treeView );
		}
		Scene scene = new Scene( stackPane );
		stage.setScene( scene );
		stage.show();
	}

	//==============================================================================================
	
	private static TreeItem< String > createSolutionItem( SolutionFile solutionFile ) {
		TreeItem< String > solutionItem = new TreeItem<>( solutionFile.path().getFileName().toString() );
		solutionItem.setExpanded( true );
		solutionItem.setGraphic( getIcon( SLN_ICON_NAME ));
		
		return solutionItem;
	}
	
	//==============================================================================================
	
	private static void buildProjectTree( TreeItem< String > parentItem , ProjectFile projectFile  ) throws Exception {
		TreeItem< String > projectItem = new TreeItem<>( projectFile.path().getFileName().toString() );
		projectItem.setGraphic( getProjectIcon( projectFile ));
		parentItem.getChildren().add( projectItem );

		for ( ProjectFile projectReference : projectFile.getProjectReferences() ) {
			for ( String libraryReference : projectFile.getLibraryReferences() ) {
				TreeItem< String > libraryItem = new TreeItem<>( libraryReference );
				libraryItem.setGraphic( getIcon( LIB_ICON_NAME ));
				parentItem.getChildren().add( libraryItem );
			}
			buildProjectTree( projectItem, projectReference );
		}
	}
	
	//==============================================================================================
	
	private static ImageView getProjectIcon( ProjectFile projectFile ) {
		if ( projectFile instanceof CPlusPlusProjectFile )
			return getIcon( CPP_ICON_NAME );
		else if ( projectFile instanceof CSharpProjectFile )
			return getIcon( CS_ICON_NAME );
		else
			return null;
	}
	
	//==============================================================================================
	
	private static ImageView getIcon( String iconName ) {
		return new ImageView( new Image( Main.class.getResourceAsStream( iconName )));
	}
}
