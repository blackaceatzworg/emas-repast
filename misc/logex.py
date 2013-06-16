#!/usr/bin/env python

import math


def parse_logs(fname):
	with open(fname) as f:
		lines = f.read().split('#')
		header = lines[0].split('\n')[0]
		lines = lines[1:]
		lines = [e.strip() for e in lines if e.strip()]
		logs = []
		for run in lines:
			run = run.split('\n')
			ratio = float(run[-1].split(':')[1])
			travcnt = int(run[-2].split(':')[1])
			avgtime = float(run[-3].split(':')[1])
			maxtime = float(run[-4].split(':')[1])
			log = []
			for node in run[:-4]:
				t, trav = node.split('=')[1].split(' ')
				t = float(t[:-len(', ')])
				trav = int(trav[:-len('travels')])
				log.append((t, trav))
			logs.append((avgtime, maxtime, travcnt, ratio, log))
		return header, logs


def extract_data(fname):
	header, logs = parse_logs(fname)
	'''
	logi w formacie listy krotek 
		[(avgtime, maxtime, travelcount, travel/sec_ratio,
			[(node1time, node1travels), (node2time, node2travels), ...])
		]
	'''
	# for tup in logs:
	#	 print tup
	logs = [[tup[k] for tup in logs] for k in range(4)]
	names = ['avgtimes', 'maxtimes', 'travelcounts', 'trav/sec ratio']
	# for name, log in zip(names, logs):
	#	 print name, log
	means = [sum(p)/float(len(p)) for p in logs]
	diffs = [[(e-means[i])**2 for e in logs[i]] for i in range(4)]
	stds = [math.sqrt(sum(diff)) for diff in diffs]
	# print 'mean values', means
	# print '(values minus mean)^2', diffs
	# print 'standard deviations', stds
	return header, means, stds


def main():
	fnames = ['waznedane1.txt', 'waznedane2.txt', 'waznedane5.txt']
	results = [extract_data(fname) for fname in fnames]
	names = ['avgtimes', 'maxtimes', 'travelcounts', 'trav/sec ratio']
	for i in range(4):
		print names[i]
		for res in results:
			print res[0][0], res[1][i], res[2][i]
		print '####'


if __name__ == '__main__':
	main()
	
