// Copyright 2014 Peter Most, PERA Software Solutions GmbH
//
// This file is part of the JavaAidKit library.
//
// JavaAidKit is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// JavaAidKit is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with JavaAidKit. If not, see <http://www.gnu.org/licenses/>.

package com.pera_software.showprojectdependencies.visualstudio;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.pera_software.aidkit.io.Resources;

//##################################################################################################

@RunWith( Parameterized.class )
public final class SolutionFileTest
{
	private SolutionFile _solutionFile;

	@Parameters
	public static Iterable< Object[] > loadSolutionFiles()
		throws Exception
	{
		return Arrays.asList( new Object[][] {
			{ new SolutionFile( Resources.asPath(SolutionFileTest.class, "2010/Solution.sln" )) },
			{ new SolutionFile( Resources.asPath(SolutionFileTest.class, "2013/Solution.sln" )) }
		});
	}

	//==============================================================================================

	public SolutionFileTest( SolutionFile solutionFile )
	{
		_solutionFile = solutionFile;
	}

	//==============================================================================================

	@Test
	public void testFindProjects()
		throws Exception
	{
		List< ProjectFile > projectFiles = _solutionFile.loadProjects();
		assertEquals( 6, projectFiles.size() );
	}

	//==============================================================================================

	@Test
	@Ignore
	public void testFindProjectsContent()
		throws Exception
	{
//		List< Path > projectPaths = new ArrayList<>();
//		for ( ProjectFile projectFile : _solutionFile.findProjects() ) {
//			projectPaths.add( projectFile.path() );
//		}
//		assertTrue( projectPaths.contains( CustomCPlusPlusProjectFileTest.PATH ));
//		assertTrue( projectPaths.contains( DefaultCPlusPlusProjectFileTest.PATH));
//		assertTrue( projectPaths.contains( CustomCSharpProjectFileTest.PATH ));
//		assertTrue( projectPaths.contains( DefaultCSharpProjectFileTest.PATH ));
	}

	//==============================================================================================

	@Test
	@Ignore
	public void testIsCPlusPlusProjectFilePath()
	{
//		Path cplusPlusProjectPath = DefaultCPlusPlusProjectFileTest.PATH;
//		assertTrue( SolutionFile.isCPlusPlusProjectFilePath( cplusPlusProjectPath ));
	}

	//==============================================================================================

	@Test
	@Ignore
	public void testIsCSharpProjectFilePath()
	{
//		Path csharpProjectPath = DefaultCSharpProjectFileTest.PATH;
//		assertTrue( SolutionFile.isCSharpProjectFilePath( csharpProjectPath ));
	}
}
