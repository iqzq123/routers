
import time
#http://www.daniweb.com/code/snippet368.html
def print_timing(func):
    def wrapper(*arg):
        t1 = time.time()
        res = func(*arg)
        t2 = time.time()
        print '%s took %0.3f ms' % (func.func_name, (t2-t1)*1000.0)
        return res
    return wrapper

def gmtimestr():
    return time.strftime("%Y-%m-%d_%H-%M-%S", time.gmtime())
