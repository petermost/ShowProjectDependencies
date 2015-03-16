package com.pera_software;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.*;
import javafx.stage.FileChooser.*;
import com.pera_software.aidkit.javafx.scene.*;
import com.pera_software.aidkit.javafx.scene.control.*;
import com.pera_software.aidkit.visualstudio.*;
import com.pera_software.company.*;
import com.pera_software.company.javafx.*;
import com.pera_software.items.*;
import com.pera_software.aidkit.javafx.application.Application;

//##################################################################################################

public class ApplicationMainScene extends MainScene {

	public ApplicationMainScene( Stage stage ) throws Exception {
		super( stage );
		setWindowTitle( String.format( "%s - %s", "Project dependencies", PERA.COPYRIGHT_LINE ));
		setWindowIcon( new Image( PERA.getResourceAsStream( PERA.ICON_NAME )));

		menuBar().getMenus().addAll( createFileMenu( stage ), createHelpMenu() );

		SolutionFile solutionFile = new SolutionFile( Paths.get( Settings.solutionFileNames().get( 0 )));
		SolutionTreeItem solutionItem = new SolutionTreeItem( solutionFile );

		setCentralNode( new TreeView<>( solutionItem ));
		show();

		buildSolutionTree( solutionFile, solutionItem );
	}

	//==============================================================================================

	private Menu createFileMenu( Stage stage ) {
		MenuItem open = new OpenMenuItem();
		open.setOnAction( event -> onOpen( event, stage ));

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
		AboutDialog dialog = new AboutDialog( Application.instance().getHostServices() );

		dialog.showAndWait();
	}

	//==============================================================================================

	private void onOpen( @SuppressWarnings( "unused" ) ActionEvent event, Window ownerWindow ) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle( "Choose solution file" );
		chooser.getExtensionFilters().add( new ExtensionFilter( "Solution Files", "*.sln" ));
		File selectedSolutionFile = chooser.showOpenDialog( ownerWindow );
		if ( selectedSolutionFile != null ) {
			try {
				showDependencies( selectedSolutionFile.toPath() );
			} catch ( Exception exception ) {
				// TODO Auto-generated catch block
				exception.printStackTrace();
			}
		}
	}

	//==============================================================================================

	private void onExit( @SuppressWarnings( "unused" ) ActionEvent event ) {
		Platform.exit();
	}

	//==============================================================================================

	public void showDependencies( Path solutionFilePath ) throws Exception {
		SolutionFile solutionFile = new SolutionFile( solutionFilePath );
		SolutionTreeItem solutionItem = new SolutionTreeItem( solutionFile );

		setCentralNode( new TreeView<>( solutionItem ));
		buildSolutionTree( solutionFile, solutionItem );
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
				Platform.runLater( () -> statusBar().setText( String.format( "Finished loading %d projects", projectFiles.size() )));
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