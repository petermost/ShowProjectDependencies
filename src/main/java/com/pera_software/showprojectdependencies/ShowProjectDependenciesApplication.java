package com.pera_software.showprojectdependencies;

import java.nio.file.Paths;

import com.pera_software.aidkit.javafx.application.Application;
import com.pera_software.company.PERA;

import javafx.scene.image.Image;
import javafx.stage.Stage;

//##################################################################################################

public class ShowProjectDependenciesApplication extends Application {

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

		ApplicationMainScene scene = new ApplicationMainScene( stage );
		stage.setScene( scene );
		stage.show();

		scene.showDependencies( Paths.get( Settings.solutionFileNames().get( 0 )));
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
