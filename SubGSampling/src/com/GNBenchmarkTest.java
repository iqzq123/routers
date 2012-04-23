package com;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tseg.algorithm.community.CommunityDetection;
import org.tseg.algorithm.community.cnm.CNM;
import org.tseg.algorithm.community.gn.GN;
import org.tseg.algorithm.community.link.MultiScaleLinkPartition;
import org.tseg.algorithm.community.local.LocalMetricCommunity;
import org.tseg.algorithm.community.local.TestLocalMetricCommunity;
import org.tseg.algorithm.community.local.refine.CommunityRefinement;
import org.tseg.algorithm.community.louvain.BGLLCommunityDetection;
import org.tseg.algorithm.community.louvain.onepass.BGLLCommunityOnePass;
import org.tseg.algorithm.community.lpa.NaiveLablePropagation;
import org.tseg.algorithm.community.mcl.MarkovClustering;
import org.tseg.algorithm.community.metric.global.GlobalQualityMetric;
import org.tseg.algorithm.community.metric.global.NGModularity;
import org.tseg.algorithm.community.similar.PartitionSimilarity;
import org.tseg.algorithm.community.similar.infor.NormalizedMutualInformation;
import org.tseg.algorithm.community.similar.match.DongenMetric;
import org.tseg.algorithm.community.similar.pair.JaccardIndex;
import org.tseg.algorithm.converter.OverlapToNonoverlapCommunity;
import org.tseg.algorithm.model.community.CommunityGenerator;
import org.tseg.algorithm.model.community.GNCommunityGraphGenerator;
import org.tseg.extern.Evaluation;
import org.tseg.graph.Graph;
import org.tseg.graph.UndigraphImpl;
import org.tseg.graph.community.VertexCommunity;
import org.tseg.graph.community.VertexCommunityImpl;
import org.tseg.graph.vertex.Vertex;
import org.tseg.io.NaiveFileGraphReader;
import org.tseg.utils.GlobalEnvValue;
import org.tseg.visual.cluster.Clusters;
import org.tseg.visual.cluster.ClustersImpl;
import org.tseg.visual.control.ColorChange;

public class GNBenchmarkTest 
{




	static public  void testSample( BufferedWriter writer,double outRatio, int turn) throws IOException
	{
		
		double jaccardIndex = 0;
		double nmiScore = 0;
		double dongenScore = 0;
		double sumQ = 0;
		double sumStandQ = 0;
		
		int communityNumber = 4;
		int communityNode = 32;
		double avgDegree = 16;
		
		for (int i = 0; i < turn; i++)
		{
			double inDegree = avgDegree - outRatio;
			
			double inDegreeRate = inDegree / avgDegree;
			
			CommunityGenerator generator 
			= new GNCommunityGraphGenerator(communityNumber, communityNode, avgDegree, inDegreeRate);
			
			Graph graph = generator.generate();
			Collection standardCommunities = generator.getCommunities();
			

			
	
			
			TopicCom c = new TopicCom();
			c.read(graph);
			c.run(120,4);
			
			Collection topicCommunities = c.getCommunities(graph);
			PartitionSimilarity similarity1 = new JaccardIndex();
			PartitionSimilarity similarity2 = new NormalizedMutualInformation();
			PartitionSimilarity dongenMetric = new DongenMetric();
			double score1 = similarity1.getSimilarity(graph, topicCommunities, standardCommunities);
			double score2 = similarity2.getSimilarity(graph, topicCommunities, standardCommunities);
			double score3 = dongenMetric.getSimilarity(graph, topicCommunities, standardCommunities);
			//System.out.println("Score" + score);
			jaccardIndex += score1;
			nmiScore += score2;
			dongenScore += (1 - score3);
			
			GlobalQualityMetric gnModularity = new NGModularity();		
			double module = gnModularity.getQualityScore(graph, topicCommunities);
			sumQ += module;
			
			GlobalQualityMetric gnModularity2 = new NGModularity();		
			double module2 = gnModularity2.getQualityScore(graph, standardCommunities);
			sumStandQ += module2;
			//System.out.println("Score" + score);
		}
		jaccardIndex /= turn;
		nmiScore /= turn;
		dongenScore /= turn;
		sumQ  /= turn;
		sumStandQ /= turn;	
		System.out.println(outRatio + "\t" + nmiScore + "\t" + jaccardIndex + "\t" + dongenScore + "\t" + sumQ);
		writer.write(outRatio + "\t" + nmiScore + "\t" + jaccardIndex + "\t" + dongenScore + "\n");
		writer.flush();
	}
	static public  void getCommunities( BufferedWriter writer,double outRatio, int turn) throws IOException
	{
		
		double jaccardIndex = 0;
		double nmiScore = 0;
		double dongenScore = 0;
		double sumQ = 0;
		double sumStandQ = 0;
		
		int communityNumber = 8;
		int communityNode = 256;
		double avgDegree = 12;
		int sampleNum=1000;
		
		for (int i = 0; i < turn; i++)
		{
			double inDegree = avgDegree - outRatio;
			
			double inDegreeRate = inDegree / avgDegree;
			
			CommunityGenerator generator 
			= new GNCommunityGraphGenerator(communityNumber, communityNode, avgDegree, inDegreeRate);
			
			Graph graph = generator.generate();
			Collection standardCommunities = generator.getCommunities();
			

					
			TopicCom c = new TopicCom();
			c.read(graph);
			c.run(sampleNum,communityNumber);
			
			Collection topicCommunities = c.getCommunities(graph);
			PartitionSimilarity similarity1 = new JaccardIndex();
			PartitionSimilarity similarity2 = new NormalizedMutualInformation();
			PartitionSimilarity dongenMetric = new DongenMetric();
			double score1 = similarity1.getSimilarity(graph, topicCommunities, standardCommunities);
			double score2 = similarity2.getSimilarity(graph, topicCommunities, standardCommunities);
			double score3 = dongenMetric.getSimilarity(graph, topicCommunities, standardCommunities);
			//System.out.println("Score" + score);
			jaccardIndex += score1;
			nmiScore += score2;
			dongenScore += (1 - score3);
			
			GlobalQualityMetric gnModularity = new NGModularity();		
			double module = gnModularity.getQualityScore(graph, topicCommunities);
			sumQ += module;
			
			GlobalQualityMetric gnModularity2 = new NGModularity();		
			double module2 = gnModularity2.getQualityScore(graph, standardCommunities);
			sumStandQ += module2;
			//System.out.println("Score" + score);
		}
		jaccardIndex /= turn;
		nmiScore /= turn;
		dongenScore /= turn;
		sumQ  /= turn;
		sumStandQ /= turn;	
		System.out.println(outRatio + "\t" + nmiScore + "\t" + jaccardIndex + "\t" + dongenScore + "\t" + sumQ);
		writer.write(outRatio + "\t" + nmiScore + "\t" + jaccardIndex + "\t" + dongenScore + "\n");
		writer.flush();
	}
	
	public static void main(String []args) throws IOException
	{
		String outfile = "c:/nmi1.txt";
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
		for (int i = 1; i <= 1; i++)
		{
			double out = i * 0.5;
			GNBenchmarkTest.getCommunities(writer, out, 1);
		}
		writer.close();
	}
}
