library(ggplot2)
library(tidyr)
library(dplyr)
nGait = 4000

# On charge les sequences (au nombre de 24)
data <- read.table('C:/Users/tipha/OneDrive/Documents/Cours/TER/Tiphaine.txt', sep=";") %>% 
 head(n=nGait) %>% 
 as.data.frame() %>% dplyr::mutate(time=row_number()-1,
                                   norme = sqrt(V1^2+V2^2+V3^2)) %>%
  dplyr::select(c(V1,V2,V3,time, norme)) %>%
  as.data.frame()


data_lent <- data[0:1000,] %>% mutate(marche = "lent")

data_rapide <- data[1000:2000,] %>% mutate(marche="rapide")

data_normale <- data[2000:3000,] %>% mutate(marche="moyen")

data_pause <- data[3000:4000,] %>% mutate(marche="saccadee")
  

# on affiche les sequences
ggplot(data=data_pause, mapping=aes(time, norme)) +
  geom_line() +
  xlab('Time') + ylab('Acc') +
  labs(title='Marche avec pause') +
  guides(color=guide_legend(""))

# On test
data_propre <- data[2000:3000,]

ggplot(data=data_propre, mapping=aes(time, norme)) +
  geom_line() +
  xlab('Time') + ylab('Signal') +
  labs(title='Le signal') +
  guides(color=guide_legend(""))


# on fait notre transformations en frequence
i=1
len=nrow(data_lent)/2 -1
dataFreq = data.frame(freq=seq(0, len))#coefficients

sequence = (data_rapide$norme/(2*len))
sequence = sequence - mean(sequence)
dataFreq[['value']]= head(abs(fft(sequence)), len+1)

# on affiche les frequences
ggplot(data=dataFreq, mapping=aes(freq, value)) +
  geom_line() +
  xlab('Frequency') + ylab('Amplitude') +
  labs(title='Les frÃ©quences')

max <- max(dataFreq$value)

# Coeff 1 : celui qui a la plus faible : un événement sur les 20 sec
# Il faut le diviser par 20 car 1/20ème 
#1er coeff une sinusiode dont la période est le signal complet
# 1hz si les 20 sec étaient une seconde 
# Combien de fois le premier coefficients se produit
# Le 2ème element se produit 2 fois dans la séuqence donc une fois sur 10
# Sur les 20 sec on a fait 25 pas, 25/20 = 1.25Hz, ilse produit 1.25 pr sec
# Si je veux savoir combien de fois il dure 1/freq=0.8sec à faire u pas
# Evenement : nb d'événement par temps
# L'inverse de ca: nombre de temps par événement 
# Hz unité d'évenemnt par sec
# Coeff 1 : qui décrit une temporalité 

1/max

# Le max correspond à 26/20=1.3hz la personne qui marchait tous les 1.8s

df_inter <- dataFreq %>% filter(value == max)
df_marche_saccade <- data.frame(
  amplitude = max,
  frequ = df_inter$freq,
  rythme = df_inter$freq/20
  
)
1/2.15
