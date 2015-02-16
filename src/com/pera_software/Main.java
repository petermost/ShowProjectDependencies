package com.pera_software;

import java.nio.file.*;
import java.util.*;
import org.controlsfx.control.*;
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

	private static final String SLN_ICON_NAME = "icons16x16/visual-studio-solution.png";
	private static final String CPP_ICON_NAME = "icons16x16/text-x-c++src.png";
	private static final String CS_ICON_NAME = "icons16x16/text-x-csharp.png";
	private static final String LIB_ICON_NAME = "icons16x16/code-class.png";
	private static final String NODE_ICON_NAME = "icons16x16/folder-yellow.png";
	private static final String EXIT_ICON_NAME = "icons16x16/application-exit.png";

	private static final Image SLN_IMAGE = new Image( Main.class.getResourceAsStream( SLN_ICON_NAME ));
	private static final Image CPP_IMAGE = new Image( Main.class.getResourceAsStream( CPP_ICON_NAME ));
	private static final Image CS_IMAGE  = new Image( Main.class.getResourceAsStream( CS_ICON_NAME ));
	private static final Image LIB_IMAGE = new Image( Main.class.getResourceAsStream( LIB_ICON_NAME ));
	private static final Image NODE_IMAGE = new Image( Main.class.getResourceAsStream( NODE_ICON_NAME ));
	private static final Image EXIT_IMAGE = new Image( Main.class.getResourceAsStream( EXIT_ICON_NAME ));

	private static final Map< String, Image > ICONS = new HashMap<>();
	static {
		ICONS.put( SLN_ICON_NAME, SLN_IMAGE );
		ICONS.put( CPP_ICON_NAME, CPP_IMAGE );
		ICONS.put( CS_ICON_NAME,  CS_IMAGE  );
		ICONS.put( LIB_ICON_NAME, LIB_IMAGE );
		ICONS.put( NODE_ICON_NAME, NODE_IMAGE );
		ICONS.put( EXIT_ICON_NAME, EXIT_IMAGE );
	}

	private Stage _stage;
	private StatusBar _statusBar;

	private static ImageView getIcon( String iconName ) {
		return new ImageView( ICONS.get( iconName ));
	}

	//==============================================================================================

	private static TreeItem< String > createSolutionItem( SolutionFile solutionFile ) {
		TreeItem< String > solutionItem = new TreeItem<>( solutionFile.path().getFileName().toString() );
		solutionItem.setExpanded( true );
		solutionItem.setGraphic( getIcon( SLN_ICON_NAME ));

		return solutionItem;
	}


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

		_statusBar = new StatusBar();

		SolutionFile solutionFile = new SolutionFile( Paths.get( Settings.solutionFileNames().get( 0 )));
		TreeItem< String > solutionItem = createSolutionItem( solutionFile );

		BorderPane borderPane = new BorderPane();
		borderPane.setTop( menuBar );
		borderPane.setCenter( new TreeView<>( solutionItem ));
		borderPane.setBottom( _statusBar );

		Scene scene = new Scene( borderPane );
		stage.setScene( scene );
		stage.show();

		buildSolutionTree( solutionFile, solutionItem );
	}


	//==============================================================================================


	private Menu createFileMenu() {
		MenuItem open = new MenuItem( "_Open..." );
		open.setOnAction( this::onOpen );

		MenuItem exit = new MenuItem( "E_xit" );
		exit.setGraphic( getIcon( EXIT_ICON_NAME ));
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

	private void buildSolutionTree( SolutionFile solutionFile, TreeItem< String > solutionItem ) throws Exception {
		List< ProjectFile > projectFiles = solutionFile.loadProjects();
		Thread builder = new Thread(() -> {
			try {
				for ( ProjectFile projectFile : projectFiles ) {
					buildProjectTree( solutionFile, solutionItem, projectFile );
				}
				Platform.runLater( () -> _statusBar.setText( String.format( "Finished loading %d projects", projectFiles.size() )));
			} catch ( Exception exception ) {
				exception.printStackTrace();
			}
		});
		builder.start();
	}

	//==============================================================================================

	private static void buildLibraryBranch( TreeItem< String > parentItem, List< String > libraryNames )
	{
		if ( !libraryNames.isEmpty() ) {
			TreeItem< String > librariesItem = new TreeItem<>( "Libraries" );
			librariesItem.setGraphic( getIcon( NODE_ICON_NAME ));
			parentItem.getChildren().add( librariesItem );

			for ( String libraryName : libraryNames ) {
				TreeItem< String > libraryItem = new TreeItem<>( libraryName );
				libraryItem.setGraphic( getIcon( LIB_ICON_NAME ));
				librariesItem.getChildren().add( libraryItem );
			}
		}
	}

	//==============================================================================================

	private static void buildProjectTree( SolutionFile solutionFile, TreeItem< String > parentItem ,
		ProjectFile projectFile  ) throws Exception {

		TreeItem< String > projectItem = new TreeItem<>( projectFile.path().getFileName().toString() );
		projectItem.setGraphic( getProjectIcon( projectFile ));

		parentItem.getChildren().add( projectItem );

		buildLibraryBranch( projectItem, projectFile.getLibraryReferences() );
		for ( Path referencedProjectPath : projectFile.getProjectReferences() ) {
			ProjectFile projectReference = solutionFile.findProject( referencedProjectPath );
			buildProjectTree( solutionFile, projectItem, projectReference );
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
}
