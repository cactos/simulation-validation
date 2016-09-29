library(devEMF)
baseDir <- "C:/users_local/krach/projects/cactos/files/04_Workpackages/WP_6/01_SimulationModels/D6.5/OS2-B-d6_5-final/"
measuredDir <- paste(baseDir, "measured_csv/", sep = "")
simulationDir <- paste(baseDir, "predicted_csv/", sep = "")
cn1.filename.sim <- "x86_64_32_in_Rack_rack01_Node_computenode08_Utilization_of_Active_Resource_Tuple.csv"
cn2.filename.sim <- "x86_64_32_in_Rack_rack01_Node_computenode14_Utilization_of_Active_Resource_Tuple.csv"
cn1.filename.meas <- "computenode08_Utilization_of_Active_Resource_Tuple.csv"
cn2.filename.meas <- "computenode14_Utilization_of_Active_Resource_Tuple.csv"

windowSizeMeasured <- 10
plotResolution <- 50
min.y <- 0

# rollmean_r from http://stackoverflow.com/questions/20134823/r-faster-way-to-calculate-rolling-statistics-over-a-variable-interval
rollmean_r = function(x,y,xout,width) {
  out = numeric(length(xout))
  for( i in seq_along(xout) ) {
    window = x >= (xout[i]-width) & x <= (xout[i]+width)
    out[i] = .Internal(mean( y[window] ))
  }
  return(out)
}

rollmean_of_df <- function(xy) {
  force(xy)
  return (function(x) {
    return (rollmean_r(x = xy[[1]], y = xy[[2]], xout = c(x), width = windowSizeMeasured))
  })
}

max.x <- 0
max.y <- 0


cn1.sim.df <- read.csv(file=paste(simulationDir, cn1.filename.sim, sep = ""))
cn1.sim.apfun <- approxfun(x = cn1.sim.df[[1]], y = cn1.sim.df[[2]], rule = 2, method = "constant")
max.x <- max(max.x, max(cn1.sim.df[[1]]))
max.y <- max(max.y, max(cn1.sim.df[[2]]))

cn2.sim.df <- read.csv(file=paste(simulationDir, cn2.filename.sim, sep = ""))
cn2.sim.apfun <- approxfun(x = cn2.sim.df[[1]], y = cn2.sim.df[[2]], rule = 2, method = "constant")
max.x <- max(max.x, max(cn2.sim.df[[1]]))
max.y <- max(max.y, max(cn2.sim.df[[2]]))

cn1.meas.df <- read.csv(file=paste(measuredDir, cn1.filename.meas, sep = ""))
cn1.meas.apfun <- rollmean_of_df(cn1.meas.df)
max.x <- max(max.x, max(cn1.meas.df[[1]]))
max.y <- max(max.y, max(cn1.meas.df[[2]]))

cn2.meas.df <- read.csv(file=paste(measuredDir, cn2.filename.meas, sep = ""))
cn2.meas.apfun <- rollmean_of_df(cn2.meas.df)
max.x <- max(max.x, max(cn2.meas.df[[1]]))
max.y <- max(max.y, max(cn2.meas.df[[2]]))

max.x <- 15000



#energy.sim <- (integrate(sim.func, 0.0, max.x, subdivisions = 10000))$value
#energy.meas <- (integrate(meas.func, 0.0, max.x, subdivisions = 10000))$value

colors <- rainbow(4)
lwd <- 1.5

max.y <- 0.05
#emf(file="powerconsumption_detail_cn08_os2-b.emf", height = 8, width = 12, family = "Calibri", pointsize = 20)
pdf("powerconsumption_detail_cn08_os2-b.pdf", height = 4, width = 7)
plot(cn1.sim.df, type="n", xlim=c(0, max.x), ylim=c(min.y, max.y), xlab="Experiment time (s)", ylab="CPU Utilisation")
lines(y=sapply(seq(0, max.x, plotResolution), cn1.sim.apfun), x=seq(0, max.x, plotResolution), lwd=lwd, type="l", col=colors[1])
lines(y=sapply(seq(0, max.x, plotResolution), cn1.meas.apfun), x=seq(0, max.x, plotResolution), lwd=lwd, type="l", col=colors[3])
abline(v=c(2906, 7036), lty=2, col = colors[1], lwd=lwd) # Simulation placements on CN08
abline(v=c(2868, 6875),lty=5, col = colors[3], lwd=lwd) # Real placements on CN08
abline(v=c(3541), lty=2, lwd=lwd)
abline(v=c(4599), lty=5, lwd=lwd)
legend(10000, max.y, c("CN08 Predicted", "CN08 Measured"), lty=c(1,1), lwd=c(2.5,2.5), col=c(colors[1], colors[3]))
dev.off()

max.y <- 0.1
#emf(file="powerconsumption_detail_cn14_os2-b.emf", height = 8, width = 12, family = "Calibri", pointsize = 20)
pdf("powerconsumption_detail_cn14_os2-b.pdf", height = 4, width = 7)
plot(cn1.sim.df, type="n", xlim=c(0, max.x), ylim=c(min.y, max.y), xlab="Experiment time (s)", ylab="CPU Utilisation")
lines(y=sapply(seq(0, max.x, plotResolution), cn2.sim.apfun), x=seq(0, max.x, plotResolution), lwd=lwd, type="l", col=colors[2]) 
lines(y=sapply(seq(0, max.x, plotResolution), cn2.meas.apfun), x=seq(0, max.x, plotResolution), lwd=lwd, type="l", col=colors[4]) 
abline(v=c(1015, 5439, 7359, 8013), lty=2, col = colors[2], lwd=lwd) # Simulation placements on CN14
abline(v=c(971, 5401, 7315), lty=5, col = colors[4], lwd=lwd) # Real placements on CN14
abline(v=c(3541, 9241), lty=2, lwd=lwd)
abline(v=c(4599), lty=5, lwd=lwd)
legend(10000, max.y, c("CN14 Predicted", "CN14 Measured"), lty=c(1,1), lwd=c(2.5,2.5), col=c(colors[2], colors[4]))
dev.off()