setwd("C:\\Users\\stier\\workspaceNested\\eu.cactosfp7.cactosim.evaluation.dataplay.dtp1_simulate")
react <- read.csv("autoscalingReact.csv")
setwd("C:\\Users\\stier\\workspaceNested\\eu.cactosfp7.cactosim.evaluation.dataplay.dtp1_simulate_alternative")
reg <- read.csv("autoscalingReg.csv")

png(filename = "comparison.png",
    width = 960, height = 600)
plot(react[[1]], react[[2]], type="n", col="red", xlim=c(0,3120), ylim=c(0,7), xlab="Simulation Time (in s)", ylab="Number of Master Instances",
     cex.lab=1.5, cex.axis=1.5, cex.main=1.5, cex.sub=1.5)
lines(c(c(0,react[[1]]), 3120), c(c(1,react[[2]]),react[[2]][length(react[[2]])]), type="s", col="red")
lines(c(c(0,reg[[1]]), 3120), c(c(1,reg[[2]]),reg[[2]][length(reg[[2]])]), type="s", col="blue")
legend(2700,7, c("React","Reg"),lty=c(1,1),col= c("red", "blue"), cex=1.5)
dev.off()