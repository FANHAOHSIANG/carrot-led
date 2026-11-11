package tw.carrot.led;
public final class TripMetricsTests {
  static void check(boolean v){if(!v)throw new AssertionError("GPS quality filter");}
  public static void main(String[] args){
    TripMetrics m=new TripMetrics();
    check(m.record(10,5,0,null,null));check(m.speedKmh==36&&m.metres==0);
    check(m.record(10,5,0,10.,1.));check(m.metres==10);
    m.record(0,5,0,5.,1.);m.record(10,5,0,1000.,1.);check(m.metres==10);
    check(!m.record(10,5,11,10.,1.));check(!m.record(10,100,0,10.,1.));
    m.record(-1,5,0,10.,1.);check(m.speedKmh==null&&m.metres==10);
    check(!m.record(10,Double.NaN,0,10.,1.));check(!m.record(10,5,-2,10.,1.));
    m.record(10,5,0,10.,0.);m.record(10,5,0,10.,11.);check(m.metres==10);
    System.out.println("GPS: iOS quality-filter scenarios, stale/poor/jitter/jump/invalid fixes passed");
  }
}
