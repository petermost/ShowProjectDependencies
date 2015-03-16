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
import org.controlsfx.control.*;
import com.pera_software.aidkit.javafx.standard.*;
import com.pera_software.aidkit.visualstudio.*;
import com.pera_software.company.*;
import com.pera_software.company.javafx.*;
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
		stage.getIcons().add( new Image( PERA.getResourceAsStream( PERA.ICON_NAME )));

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll( createFileMenu(), createHelpMenu() );

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

	private Menu createFileMenu() {
		MenuItem open = new OpenMenuItem();
		open.setOnAction( this::onOpen );

		MenuItem exit = new ExitMenuItem();
		exit.setOnAction( this::onExit );

		Menu fileMenu = new Menu( "_File" );
		fileMenu.getItems().addAll( open, exit );

		return fileMenu;
	}

	//==============================================================================================

	private Menu createHelpMenu() {
		MenuItem aboutPERA = new AboutMenuItem();
		aboutPERA.setOnAction( this::onAboutPERA );

		Menu helpMenu = new Menu( "_Help" );
		helpMenu.getItems().addAll( aboutPERA );

		return helpMenu;
	}

	//==============================================================================================

	private void onAboutPERA( @SuppressWarnings( "unused" ) ActionEvent event ) {
		AboutDialog dialog = new AboutDialog( getHostServices() );

		dialog.showAndWait();
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
				throw new Error( exception );
			}
		});
		builder.start();
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
}

//private void buildSolutionTree2( SolutionFile solutionFile, TreeItem< String > solutionItem ) throws Exception {
//	ExecutorService executor = Executors.newSingleThreadExecutor();
//	Future< Void > builderFuture = executor.submit(() -> {
//		List< ProjectFile > projectFiles = solutionFile.loadProjects();
//		sortByProjectName( projectFiles );
//		for ( ProjectFile projectFile : projectFiles ) {
//			buildProjectTree( solutionFile, solutionItem, projectFile );
//		}
//		Platform.runLater( () -> _statusBar.setText( String.format( "Finished loading %d projects", projectFiles.size() )));
//
//		return null;
//	});
//	builderFuture.get();
//}
