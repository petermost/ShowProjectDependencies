package com.pera_software.showprojectdependencies;

import static com.pera_software.aidkit.lang.Exceptions.tryProcedure;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.pera_software.aidkit.javafx.application.Application;
import com.pera_software.aidkit.javafx.scene.MainScene;
import com.pera_software.aidkit.javafx.scene.control.ExitMenuItem;
import com.pera_software.aidkit.javafx.scene.control.OpenMenuItem;
import com.pera_software.company.javafx.AboutDialog;
import com.pera_software.company.javafx.AboutMenuItem;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

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
