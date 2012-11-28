#!/usr/bin/env python

import sys
import string
import common.dump
from common.file import myopen
from common.stats import stats
from hyperparameters import *
import miscglobals
import logging

import examples
#import diagnostics
import state
import corrupt

def validate(cnt):
    import math
    logranks = []
    logging.info("BEGINNING VALIDATION AT TRAINING STEP %d" % cnt)
    logging.info(stats())
    i = 0
    for (i, ve) in enumerate(examples.get_validation_example()):
#        logging.info([wordmap.str(id) for id in ve])
        logranks.append(math.log(m.validate(ve)))
        if (i+1) % 10 == 0:
            logging.info("Training step %d, validating example %d, mean(logrank) = %.2f, stddev(logrank) = %.2f" % (cnt, i+1, numpy.mean(numpy.array(logranks)), numpy.std(numpy.array(logranks))))
            logging.info(stats())
    logging.info("FINAL VALIDATION AT TRAINING STEP %d: mean(logrank) = %.2f, stddev(logrank) = %.2f, cnt = %d" % (cnt, numpy.mean(numpy.array(logranks)), numpy.std(numpy.array(logranks)), i+1))
    logging.info(stats())
#    print "FINAL VALIDATION AT TRAINING STEP %d: mean(logrank) = %.2f, stddev(logrank) = %.2f, cnt = %d" % (cnt, numpy.mean(numpy.array(logranks)), numpy.std(numpy.array(logranks)), i+1)
#    print stats()

if __name__ == "__main__":
    #import common.hyperparameters, common.options
    #HYPERPARAMETERS = common.hyperparameters.read("language-model")
    #HYPERPARAMETERS, options, args, newkeystr = common.options.reparse(HYPERPARAMETERS)
    

    
    import sys
    #print >> sys.stderr, myyaml.dump(common.dump.vars_seq([hyperparameters, miscglobals]))

    import noise
    import vocabulary   
    import model
    #indexed_weights = noise.indexed_weights()
  
    logfile ='log'
    
    import random, numpy
    random.seed(miscglobals.RANDOMSEED)
    numpy.random.seed(miscglobals.RANDOMSEED)



   
    print >> sys.stderr, ("INITIALIZING")
    m = model.Model()
    cnt = 0
    epoch = 1
    get_train_minibatch = examples.TrainingMinibatchStream()
    logging.basicConfig(filename=logfile, filemode="w", level=logging.DEBUG)
    logging.info("INITIALIZING TRAINING STATE")

    while 1:
        logging.info("STARTING EPOCH #%d" % epoch)
        for ebatch in get_train_minibatch:
            cnt += len(ebatch)
        #    print [wordmap.str(id) for id in e]

            noise_sequences, weights = corrupt.corrupt_examples(m, ebatch)
            m.train(ebatch, noise_sequences, weights)

            #validate(cnt)
            if cnt % (int(1000./HYPERPARAMETERS["MINIBATCH SIZE"])*HYPERPARAMETERS["MINIBATCH SIZE"]) == 0:
                logging.info("Finished training step %d (epoch %d)" % (cnt, epoch))
#                print ("Finished training step %d (epoch %d)" % (cnt, epoch))
            if cnt % (int(100000./HYPERPARAMETERS["MINIBATCH SIZE"])*HYPERPARAMETERS["MINIBATCH SIZE"]) == 0:
#                diagnostics.diagnostics(cnt, m)
                if os.path.exists(os.path.join(rundir, "BAD")):
                    logging.info("Detected file: %s\nSTOPPING" % os.path.join(rundir, "BAD"))
                    sys.stderr.write("Detected file: %s\nSTOPPING\n" % os.path.join(rundir, "BAD"))
                    sys.exit(0)
            if cnt % (int(HYPERPARAMETERS["VALIDATE_EVERY"]*1./HYPERPARAMETERS["MINIBATCH SIZE"])*HYPERPARAMETERS["MINIBATCH SIZE"]) == 0:
                state.save(m, cnt, epoch, get_train_minibatch, rundir, newkeystr)
#                diagnostics.visualizedebug(cnt, m, rundir, newkeystr)
#                validate(cnt)
        get_train_minibatch = examples.TrainingMinibatchStream()
        epoch += 1
