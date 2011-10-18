/*******************************************************************************
 * Copyright (c) 2011 Dipanjan Das 
 * Language Technologies Institute, 
 * Carnegie Mellon University, 
 * All Rights Reserved.
 * 
 * TrainingMain.java is part of SEMAFOR 2.0.
 * 
 * SEMAFOR 2.0 is free software: you can redistribute it and/or modify  it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * SEMAFOR 2.0 is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License along
 * with SEMAFOR 2.0.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package edu.cmu.cs.lti.ark.fn.parsing;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import edu.cmu.cs.lti.ark.fn.utils.FNModelOptions;
import edu.cmu.cs.lti.ark.util.SerializedObjects;

public class MaxMarginTrainingMain
{
	public static void main(String[] args)
	{
		FNModelOptions opts = new FNModelOptions(args);
		String modelFile = opts.modelFile.get();
		String alphabetFile = opts.alphabetFile.get();
		String frameFeaturesCacheFile = opts.frameFeaturesCacheFile.get();
		String frFile = opts.trainFrameFile.get();
		int totalpasses = opts.totalPasses.get();
		String reqFile = opts.reqFile.get();
		String exFile = opts.exFile.get();
		boolean costAugmented = opts.costAugmented.get().equals("yes");
		String decodingType = opts.decodingType.get();
		ArrayList<FrameFeatures> list = getFFList(frameFeaturesCacheFile);
		MaxMarginTraining bpt = new MaxMarginTraining(decodingType);
		bpt.init(modelFile, alphabetFile, list, frFile);
		bpt.setMaps(reqFile, exFile);
		bpt.train(totalpasses, costAugmented);
		bpt.writeModel();
		bpt.wrapUp();
	}	
	
	public static ArrayList<FrameFeatures> getFFList(String path) {
		ArrayList<FrameFeatures> ffList = new ArrayList<FrameFeatures>();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.contains("jobj");
			}
		};
		System.out.println("Reading binary event files.");
		File f = new File(path);
		String[] list = f.list(filter);
		int i = 0;
		for (String l: list) {
			FrameFeatures fr = (FrameFeatures)SerializedObjects.readSerializedObject(f.getAbsolutePath() + "/" + l);
			ffList.add(fr);
			i++;
			if (i % 500 == 0) {
				System.out.print(i + " ");
			}
		}
		System.out.println();
		System.out.println("Finished reading binary event files");
		return ffList;
	}
}
