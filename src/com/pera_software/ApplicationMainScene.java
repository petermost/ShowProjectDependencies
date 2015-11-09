package com.pera_software;

import java.io.*;
import java.nio.file.*;
import javafx.application.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.stage.FileChooser.*;
import com.pera_software.aidkit.javafx.scene.*;
import com.pera_software.aidkit.javafx.scene.control.*;
import com.pera_software.company.javafx.*;
import com.pera_software.aidkit.javafx.application.Application;
import static com.pera_software.aidkit.lang.Exceptions.*;

//##################################################################################################

public class ApplicationMainScene extends MainScene {

	public ApplicationMainScene( Stage stage ) throws Exception {
		super( 800, 600 );

		menuBar().getMenus().addAll( createFileMenu( stage ), createHelpMenu() );

		Path solutionFilePath = Paths.get( Settings.solutionFileNames().get( 0 ));
		showDependencies( solutionFilePath );
	}

	//==============================================================================================

	private Menu createFileMenu( Stage stage ) throws Exception {
		MenuItem open = new OpenMenuItem();
		open.setOnAction( event -> onOpen( stage ));

		MenuItem exit = new ExitMenuItem();
		exit.setOnAction( event -> Platform.exit() );

		Menu fileMenu = new Menu( "_File" );
		fileMenu.getItems().addAll( open, exit );

		return fileMenu;
	}

	//==============================================================================================

	private static Menu createHelpMenu() throws Exception {
		MenuItem aboutPERA = new AboutMenuItem();
		aboutPERA.setOnAction( event -> tryProcedure( () -> onAboutPERA() ));

		Menu helpMenu = new Menu( "_Help" );
		helpMenu.getItems().addAll( aboutPERA );

		return helpMenu;
	}
	//==============================================================================================

	private static void onAboutPERA() throws Exception {
		AboutDialog dialog = new AboutDialog( Application.instance().getHostServices() );

		dialog.showAndWait();
	}

	//==============================================================================================

	private void onOpen( Window ownerWindow  ) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle( "Choose solution file" );
		chooser.getExtensionFilters().add( new ExtensionFilter( "Solution Files", "*.sln" ));
		File selectedSolutionFile = chooser.showOpenDialog( ownerWindow );
		if ( selectedSolutionFile != null ) {
			showDependencies( selectedSolutionFile.toPath() );
		}
	}

	//==============================================================================================

	public void showDependencies( Path solutionFilePath ) {
		ProjectDependencyTreeBuilder treeBuilder = new ProjectDependencyTreeBuilder();

		// Connect to the signals we want to show in the GUI:

		treeBuilder.projectLoaded.connect(( projectNumber, projectFileCount ) -> {
			Platform.runLater( () -> statusBar().setText( String.format( "Loaded %d of %d projects...", projectNumber, projectFileCount )));
		});
		treeBuilder.solutionItemAdded.connect(( solutionTreeItem ) -> {
			Platform.runLater( () -> setCentralNode( new TreeView<>( solutionTreeItem )));
		});
		treeBuilder.projectItemAdded.connect(( parentItem, childItem ) -> {
			Platform.runLater( () -> parentItem.getChildren().add( childItem ));
		});
		treeBuilder.librariesItemAdded.connect(( parentItem, childItem ) -> {
			Platform.runLater( () -> parentItem.getChildren().add( childItem ));
		});
		treeBuilder.libraryItemAdded.connect( ( parentItem, childItem ) -> {
			Platform.runLater( () -> parentItem.getChildren().add( childItem ));
		});
		treeBuilder.buildingFinished.connect(( projectFileCount ) -> {
			Platform.runLater( () -> statusBar().setText( String.format( "Finished loading %d projects", projectFileCount )));
		});
		treeBuilder.build( solutionFilePath );
	}
}
