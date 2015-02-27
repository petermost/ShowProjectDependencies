package com.pera_software;

import java.nio.file.*;
import java.util.*;
import org.controlsfx.control.*;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import com.pera_software.aidkit.visualstudio.*;
import com.pera_software.company.*;
import com.pera_software.items.*;

//##################################################################################################

public class Main extends Application {

	private StatusBar _statusBar;

	//==============================================================================================

	public static void main( String arguments[] ) {
		Settings.parseCommandLine( arguments );
		launch( arguments );
	}

	//==============================================================================================

	@Override
	public void start( Stage stage ) throws Exception {
		stage.setTitle( String.format( "%s - %s", "Project dependencies", PERA.COPYRIGHT_LINE ));
		stage.getIcons().add( PERA.icon() );

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().add( createFileMenu() );

		_statusBar = new StatusBar();

		SolutionFile solutionFile = new SolutionFile( Paths.get( Settings.solutionFileNames().get( 0 )));
		SolutionTreeItem solutionItem = new SolutionTreeItem( solutionFile );

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

	private static void sortByProjectName( List< ProjectFile > projects ) {
		Collections.sort( projects, ( left, right ) -> {
			try {
				String leftFileName = left.path().getFileName().toString();
				String rightFileName = right.path().getFileName().toString();
				return leftFileName.compareTo( rightFileName );
			} catch ( Exception exception ) {
				throw new Error( exception );
			}
		});
	}

	//==============================================================================================

	private Menu createFileMenu() {
		MenuItem open = new MenuItem( "_Open..." );
		open.setOnAction( this::onOpen );

		MenuItem exit = new MenuItem( "E_xit" );
		exit.setGraphic( Icons.get( Icons.EXIT_ICON_NAME ));
		exit.setOnAction( this::onExit );

		Menu fileMenu = new Menu( "_File" );
		fileMenu.getItems().addAll( open, exit );

		return fileMenu;
	}

	//==============================================================================================

	private void onOpen( @SuppressWarnings( "unused" ) ActionEvent event ) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle( "Choose solution file" );
		chooser.showOpenDialog( null );
	}

	//==============================================================================================

	private void onExit( @SuppressWarnings( "unused" ) ActionEvent event ) {
		Platform.exit();
	}

	//==============================================================================================

	private void buildSolutionTree( SolutionFile solutionFile, TreeItem< String > solutionItem ) throws Exception {
		List< ProjectFile > projectFiles = solutionFile.loadProjects();
		sortByProjectName( projectFiles );
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
			LibrariesTreeItem librariesItem = new LibrariesTreeItem();
			Platform.runLater( () -> parentItem.getChildren().add( librariesItem ));

			for ( String libraryName : libraryNames ) {
				LibraryTreeItem libraryItem = new LibraryTreeItem( libraryName );
				Platform.runLater( () -> librariesItem.getChildren().add( libraryItem ));
			}
		}
	}

	//==============================================================================================

	private static void buildProjectTree( SolutionFile solutionFile, TreeItem< String > parentItem ,
		ProjectFile projectFile  ) throws Exception {

		ProjectTreeItem projectItem = new ProjectTreeItem( projectFile );

		Platform.runLater( () -> parentItem.getChildren().add( projectItem ));

		for ( Path referencedProjectPath : projectFile.getProjectReferences() ) {
			ProjectFile projectReference = solutionFile.findProject( referencedProjectPath );
			buildProjectTree( solutionFile, projectItem, projectReference );
		}
		buildLibraryBranch( projectItem, projectFile.getLibraryReferences() );
	}
}
