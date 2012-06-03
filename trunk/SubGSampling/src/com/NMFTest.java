package com;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import org.tseg.algorithm.community.metric.global.GlobalQualityMetric;
import org.tseg.algorithm.community.metric.global.NGModularity;
import org.tseg.algorithm.community.similar.PartitionSimilarity;
import org.tseg.algorithm.community.similar.infor.NormalizedMutualInformation;
import org.tseg.algorithm.community.similar.match.DongenMetric;
import org.tseg.algorithm.community.similar.pair.JaccardIndex;
import org.tseg.algorithm.model.community.CommunityGenerator;
import org.tseg.algorithm.model.community.GNCommunityGraphGenerator;
import org.tseg.graph.Graph;


public class NMFTest 
{




	static public  void testSample( BufferedWriter writer,double outRatio, int turn) throws IOException
	{
		
		double jaccardIndex = 0;
		double nmiScore = 0;
		double dongenScore = 0;
		double sumQ = 0;
		double sumStandQ = 0;
		
		int communityNumber = 4;
		int communityNode = 64;
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
			c.run(20,4);
			
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
			
			/* 这里生成了标准社团 */
			Graph graph = generator.generate();
			Collection standardCommunities = generator.getCommunities();
			

			/* 这里是自己生成的社团 */
//			TopicCom c = new TopicCom();
//			c.read(graph);
//			c.run(sampleNum,communityNumber);
//			
//			Collection topicCommunities = c.getCommunities(graph);
			TopicCom_nmf c = new TopicCom_nmf();
			c.read(graph);
			c.run(sampleNum, communityNumber);
			Collection topicCommunities = c.getCommunities(graph);
			
			/* 最后到这里即可 */
			
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
			//GNBenchmarkTest.getCommunities(writer, out, 1);
			NMFTest.testSample(writer, out, 3);
		}
		writer.close();
	}
}
