package tw.carrot.led;
/** GPS trip display only; never a substitute for C4 carState. */
public final class TripMetrics {
  public double metres; public Double speedKmh;
  public boolean record(double speed,double accuracy,double age,Double distance,Double interval) {
    if(!Double.isFinite(accuracy)||accuracy<0||accuracy>50||!Double.isFinite(age)||age < -1||age>10)return false;
    speedKmh=Double.isFinite(speed)&&speed>=0&&speed<=80?speed*3.6:null;
    if(distance!=null&&interval!=null&&accuracy<=30&&speed>=1&&speed<=80&&interval>0&&interval<=10
      &&Double.isFinite(distance)&&distance>=Math.max(3,accuracy*.5)&&distance/interval<=80)metres+=distance;
    return true;
  }
}
