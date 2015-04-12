package com.pera_software;

import java.nio.file.*;
import java.util.*;
import javafx.scene.control.*;
import com.pera_software.aidkit.signal.*;
import com.pera_software.aidkit.visualstudio.*;
import com.pera_software.items.*;

public class ProjectDependencyTreeBuilder {

	public final Signal1< SolutionTreeItem > solutionItemAdded = new Signal1<>();
	public final Signal2< TreeItem< String >, ProjectTreeItem > projectItemAdded = new Signal2<>();
	public final Signal2< ProjectTreeItem, LibrariesTreeItem > librariesItemAdded = new Signal2<>();
	public final Signal2< LibrariesTreeItem, LibraryTreeItem > libraryItemAdded = new Signal2<>();
	public final Signal1< Integer > buildingFinished = new Signal1<>();

	//==============================================================================================

	public void build( Path solutionFilePath ) {
		Thread builder = new Thread(() -> {
			try {
				SolutionFile solutionFile = new SolutionFile( solutionFilePath );
				SolutionTreeItem solutionItem = new SolutionTreeItem( solutionFile );

				solutionItemAdded.emit( solutionItem );
				buildSolutionTree( solutionFile, solutionItem );
			} catch ( Exception exception ) {
				throw new RuntimeException( exception );
			}
		});
		builder.start();
	}

	//==============================================================================================

	private void buildSolutionTree( SolutionFile solutionFile, TreeItem< String > solutionItem ) throws Exception {
		List< ProjectFile > projectFiles = solutionFile.loadProjects();
		sortByProjectName( projectFiles );
		for ( ProjectFile projectFile : projectFiles ) {
			buildProjectTree( solutionFile, solutionItem, projectFile );
		}
		buildingFinished.emit( projectFiles.size() );
	}

	//==============================================================================================

	private void buildProjectTree( SolutionFile solutionFile, TreeItem< String > parentItem ,
		ProjectFile projectFile  ) throws Exception {

		ProjectTreeItem projectItem = new ProjectTreeItem( projectFile );
		projectItemAdded.emit( parentItem, projectItem );

		for ( Path referencedProjectPath : projectFile.getProjectReferences() ) {
			ProjectFile projectReference = solutionFile.findProject( referencedProjectPath );
			buildProjectTree( solutionFile, projectItem, projectReference );
		}
		buildLibraryBranch( projectItem, projectFile.getLibraryReferences() );
	}

	//==============================================================================================

	private void buildLibraryBranch( ProjectTreeItem parentItem, List< String > libraryNames ) throws Exception {
		if ( !libraryNames.isEmpty() ) {
			LibrariesTreeItem librariesItem = new LibrariesTreeItem();
			librariesItemAdded.emit( parentItem, librariesItem );

			for ( String libraryName : libraryNames ) {
				LibraryTreeItem libraryItem = new LibraryTreeItem( libraryName );
				libraryItemAdded.emit( librariesItem, libraryItem );
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
				throw new RuntimeException( exception );
			}
		});
	}

}
