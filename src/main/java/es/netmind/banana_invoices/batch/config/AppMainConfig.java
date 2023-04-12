/*
 * This code is sample code, provided as-is, and we make NO
 * warranties as to its correctness or suitability for any purpose.
 *
 * We hope that it's useful to you. Enjoy.
 * Copyright LearningPatterns Inc.
 */

package es.netmind.banana_invoices.batch.config;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.netmind.banana_invoices.batch.processor.ReciboPagadoProcessor;
import es.netmind.banana_invoices.batch.processor.ReciboValidoProcessor;
import es.netmind.banana_invoices.batch.processor.SimpleProcessor;
import es.netmind.banana_invoices.batch.reader.SimpleReader;
import es.netmind.banana_invoices.batch.writer.ReciboSimpleWriter;
import es.netmind.banana_invoices.batch.writer.SimpleWriter;
import es.netmind.banana_invoices.models.Recibo;

@Configuration
@EnableBatchProcessing
@SuppressWarnings({"rawtypes", "unchecked"})
public class AppMainConfig {
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

//    @Bean
//    ItemReader<String> simpleRead() {
//        return new SimpleReader();
//    }
//
//    @Bean
//    ItemWriter<String> simpleWrite() {
//        return new SimpleWriter();
//    }
//
//    @Bean
//    ItemProcessor<String, String> simpleProccesor() {
//        return new SimpleProcessor();
//    }
//
//    @Bean
//    public Step step1() {
//        return steps.get("step1")
//                .allowStartIfComplete(true)
//                .<String, String>chunk(2)
//                .reader(simpleRead())
//                .processor(simpleProccesor())
//                .writer(simpleWrite())
//                .build();
//    }
//
//    @Bean("mySimpleJob")
//    public Job procesadorItems() {
//        return jobs.get("job1")
//                .start(step1())
//                .build();
//    }

    // TODO: IMPLEMENT STEPS AND JOB FOR RECIBO
    @Autowired
    SynchronizedItemStreamReader s3Reader;
    
    @Bean
    CompositeItemProcessor<Recibo, Recibo> reciboProccesor() throws Exception {
    	ItemProcessor<Recibo, Recibo> processor1 = new ReciboPagadoProcessor();
    	ItemProcessor<Recibo, Object> processor2 = new ReciboValidoProcessor();
    	CompositeItemProcessor<Recibo, Recibo> compositeProcessor = new CompositeItemProcessor<>();
    	compositeProcessor.setDelegates(Arrays.asList(processor1, processor2));
    	compositeProcessor.afterPropertiesSet();
    	return compositeProcessor;
    }

    @Bean
    ItemWriter<Object> reciboWrite() {
        return new ReciboSimpleWriter();
    }

    @Bean
    public Step step2() throws Exception {
        return steps.get("step2")
                .allowStartIfComplete(true)
                .<Recibo, Recibo>chunk(1)
                .reader(s3Reader)
                //.processor(reciboProccesor())
                .writer(reciboWrite())
                .build();
    }

    @Bean("recibosJob")
    public Job procesadorRecibos() throws Exception {
        return jobs.get("job2")
                .start(step2())
                .build();
    }
    

}