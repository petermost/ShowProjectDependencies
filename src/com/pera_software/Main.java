package com.pera_software;

import java.nio.file.*;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
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
	private static final String CS_ICON_NAME = "resources/icons16x16/text-x-csharp.png";
	private static final String LIB_ICON_NAME = "resources/icons16x16/shared-lib.png";

	private static final Map< String, ImageView > ICONS = new HashMap<>();
	static {
		ICONS.put( SLN_ICON_NAME, new ImageView( SLN_ICON_NAME ));
		ICONS.put( CPP_ICON_NAME, new ImageView( CPP_ICON_NAME ));
		ICONS.put( CS_ICON_NAME,  new ImageView( CS_ICON_NAME ));
		ICONS.put( LIB_ICON_NAME, new ImageView( LIB_ICON_NAME ));
	}


	private static ImageView getIcon( String iconName ) {
		return ICONS.get( iconName );
	}

	private Stage _stage;

	//==============================================================================================

	public static void main( String arguments[] ) {
		Settings.parseCommandLine( arguments );
		launch( arguments );
	}

	//==============================================================================================

	@Override
	public void start( Stage stage ) throws Exception {
		_stage = stage;
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().add( createFileMenu() );


		BorderPane borderPane = new BorderPane();
		borderPane.setTop( menuBar );
		borderPane.setCenter( createDependencyTree( Settings.solutionFileNames().get( 0 )));

		Scene scene = new Scene( borderPane );
		stage.setScene( scene );
		stage.show();
	}

	//==============================================================================================

	private static TreeView< String > createDependencyTree( String solutionFileName ) throws Exception {
		SolutionFile solutionFile = new SolutionFile( Paths.get( solutionFileName ));
		TreeItem< String > solutionItem = createSolutionItem( solutionFile );

		for ( ProjectFile projectFile : solutionFile.findProjects() ) {
			buildProjectTree( solutionItem, projectFile );
		}
		return new TreeView<>( solutionItem );
	}

	//==============================================================================================

	private Menu createFileMenu() {
		MenuItem open = new MenuItem( "_Open..." );
		open.setOnAction( this::onOpen );

		MenuItem exit = new MenuItem( "E_xit" );
		exit.setOnAction( this::onExit );

		Menu fileMenu = new Menu( "_File" );
		fileMenu.getItems().addAll( open, exit );

		return fileMenu;
	}

	//==============================================================================================

	private void onOpen( @SuppressWarnings( "unused" ) ActionEvent event ) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle( "Choose solution file" );
		chooser.showOpenDialog( _stage );
	}

	//==============================================================================================

	private void onExit( @SuppressWarnings( "unused" ) ActionEvent event ) {
		Platform.exit();
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

		for ( ProjectFile projectReference : projectFile.getProjectReferences() ) {
			for ( String libraryReference : projectFile.getLibraryReferences() ) {
				TreeItem< String > libraryItem = new TreeItem<>( libraryReference );
				libraryItem.setGraphic( getIcon( LIB_ICON_NAME ));
				projectItem.getChildren().add( libraryItem );
			}
			buildProjectTree( projectItem, projectReference );
		}
		parentItem.getChildren().add( projectItem );
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
}
