#library(devEMF)
baseDir <- "C:/users_local/krach/projects/cactos/files/04_Workpackages/WP_6/01_SimulationModels/D6.5/OS2-B-d6_5-final/"
measuredDir <- paste(baseDir, "measured_csv/", sep = "")
measuredFilePattern <- "^(computenode)((0[^2])|(1[^6])).*(Power_Consumption_Tuple)"

simulationDir <- paste(baseDir, "predicted_csv/", sep = "")
simulationFilePattern <- "^(computenode)((0[^2])|(1[^6])).*(Power_Consumption_Tuple)"

windowSizeMeasured <- 20
plotResolution <- 50
min.y <- 400

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

meas.filenames <- list.files(path = measuredDir, pattern = measuredFilePattern)
sim.filenames <- list.files(path = simulationDir, pattern = simulationFilePattern)

max.x <- 0
max.y <- 0

sim.functions <- c()
for (i in sim.filenames) {
  df <- read.csv(file=paste(simulationDir, i, sep = ""))
  apfun <- approxfun(x = df[[1]], y = df[[2]], rule = 2)
  sim.functions <- c(apfun, sim.functions)
  max.x <- max(max.x, max(df[[1]]))
}
sim.func <- Vectorize(function(x) sum(sapply(sim.functions, do.call, args=list(x))))
max.y <- max(sim.func(seq(0, max.x, 1)))

meas.functions <- c()
for (i in meas.filenames) {
  apfun <- rollmean_of_df(read.csv(file=paste(measuredDir, i, sep = "")))
  meas.functions <- c(apfun, meas.functions)
  max.x <- max(max.x, max(df[[1]]))
}
meas.func <- Vectorize(function(x) sum(sapply(meas.functions, do.call, args=list(x))))

max.y <- max(max.y, max(meas.func(seq(0, max.x, 1))))

energy.sim <- (integrate(sim.func, 0.0, max.x, subdivisions = 10000))$value
energy.meas <- (integrate(meas.func, 0.0, max.x, subdivisions = 10000))$value

colors <- rainbow(2)

#emf(file="powerconsumption_datacentre_os2-b.emf", height = 8, width = 12, family = "Calibri", pointsize = 20)
pdf("powerconsumption_datacentre_os2-b.pdf", height = 4, width = 7)
plot(df, type="n", xlim=c(0, max.x), ylim=c(min.y, max.y), xlab="Experiment time (s)", ylab="Power Consumption")

lines(y=sapply(seq(0, max.x, plotResolution), meas.func), x=seq(0, max.x, plotResolution), lwd=1.5, type="l", col=colors[2])
lines(y=sapply(seq(0, max.x, plotResolution), sim.func), x=seq(0, max.x, plotResolution), lwd=1.5, type="l", col=colors[1])  
legend(max.x - 7500, max.y, c("Predicted", "Measured"), lty=c(1,1), lwd=c(2.5,2.5), col=colors)
dev.off()