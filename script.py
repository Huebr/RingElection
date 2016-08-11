#!/usr/bin/python

from mininet.net import Mininet
from mininet.node import Controller, RemoteController, OVSController
from mininet.node import CPULimitedHost, Host, Node
from mininet.node import OVSKernelSwitch, UserSwitch
from mininet.node import IVSSwitch
from mininet.cli import CLI
from mininet.log import setLogLevel, info
from mininet.link import TCLink, Intf
from subprocess import call

def myNetwork():

    net = Mininet( topo=None,
                   build=False,
                   ipBase='10.0.0.0/8')

    info( '*** Adding controller\n' )
    c1=net.addController(name='c1',
                      controller=Controller,
                      protocol='tcp',
                      port=6633)

    c0=net.addController(name='c0',
                      controller=Controller,
                      protocol='tcp',
                      port=6633)

    info( '*** Add switches\n')
    s9 = net.addSwitch('s9', cls=OVSKernelSwitch)
    s10 = net.addSwitch('s10', cls=OVSKernelSwitch)
    s3 = net.addSwitch('s3', cls=OVSKernelSwitch)
    s1 = net.addSwitch('s1', cls=OVSKernelSwitch)
    s6 = net.addSwitch('s6', cls=OVSKernelSwitch)
    s8 = net.addSwitch('s8', cls=OVSKernelSwitch)
    s2 = net.addSwitch('s2', cls=OVSKernelSwitch)
    s7 = net.addSwitch('s7', cls=OVSKernelSwitch)
    s5 = net.addSwitch('s5', cls=OVSKernelSwitch)
    s4 = net.addSwitch('s4', cls=OVSKernelSwitch)

    info( '*** Add hosts\n')
    h3 = net.addHost('h3', cls=Host, ip='10.0.0.3', defaultRoute=None)
    h6 = net.addHost('h6', cls=Host, ip='10.0.0.6', defaultRoute=None)
    h4 = net.addHost('h4', cls=Host, ip='10.0.0.4', defaultRoute=None)
    h5 = net.addHost('h5', cls=Host, ip='10.0.0.5', defaultRoute=None)
    h8 = net.addHost('h8', cls=Host, ip='10.0.0.8', defaultRoute=None)
    h9 = net.addHost('h9', cls=Host, ip='10.0.0.9', defaultRoute=None)
    h10 = net.addHost('h10', cls=Host, ip='10.0.0.10', defaultRoute=None)
    h11 = net.addHost('h11', cls=Host, ip='10.0.0.11', defaultRoute=None)
    h7 = net.addHost('h7', cls=Host, ip='10.0.0.7', defaultRoute=None)
    h2 = net.addHost('h2', cls=Host, ip='10.0.0.2', defaultRoute=None)
    h1 = net.addHost('h1', cls=Host, ip='10.0.0.1', defaultRoute=None)

        
    info( '*** Add links\n')
    s1h1 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s1, h1, cls=TCLink , **s1h1)
    s1h2 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s1, h2, cls=TCLink , **s1h2)
    s2h3 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s2, h3, cls=TCLink , **s2h3)
    s2h4 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s2, h4, cls=TCLink , **s2h4)
    s3h5 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s3, h5, cls=TCLink , **s3h5)
    s3h6 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s3, h6, cls=TCLink , **s3h6)
    s4h7 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s4, h7, cls=TCLink , **s4h7)
    s4h8 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s4, h8, cls=TCLink , **s4h8)
    s5h9 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s5, h9, cls=TCLink , **s5h9)
    s5h10 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s5, h10, cls=TCLink , **s5h10)
    s6h11 = {'bw':2,'delay':'50ms','loss':5}
    net.addLink(s6, h11, cls=TCLink , **s6h11)
    s6s5 = {'bw':1000,'delay':'10ms','loss':3}
    net.addLink(s6, s5, cls=TCLink , **s6s5)
    s10s5 = {'bw':1000,'delay':'10ms','loss':3}
    net.addLink(s10, s5, cls=TCLink , **s10s5)
    s10s4 = {'bw':1000,'delay':'10ms','loss':3}
    net.addLink(s10, s4, cls=TCLink , **s10s4)
    s9s4 = {'bw':1000,'delay':'10ms','loss':3}
    net.addLink(s9, s4, cls=TCLink , **s9s4)
    s9s3 = {'bw':1000,'delay':'10ms','loss':3}
    net.addLink(s9, s3, cls=TCLink , **s9s3)
    s8s3 = {'bw':1000,'delay':'10ms','loss':3}
    net.addLink(s8, s3, cls=TCLink , **s8s3)
    net.addLink(s8, s2)
    s7s2 = {'bw':1000,'delay':'10ms','loss':3}
    net.addLink(s7, s2, cls=TCLink , **s7s2)
    s7s1 = {'bw':1000,'delay':'10ms','loss':3}
    net.addLink(s7, s1, cls=TCLink , **s7s1)

    info( '*** Starting network\n')
    net.build()
    info( '*** Starting controllers\n')
    for controller in net.controllers:
        controller.start()

    info( '*** Starting switches\n')
    net.get('s9').start([c1])
    net.get('s10').start([c1])
    net.get('s3').start([c0])
    net.get('s1').start([c0])
    net.get('s6').start([c1])
    net.get('s8').start([c0])
    net.get('s2').start([c0])
    net.get('s7').start([c0])
    net.get('s5').start([c1])
    net.get('s4').start([c1])

    info( '*** Post configure switches and hosts\n')
    
    h1.sendCmd('java -jar MasterApp.jar')
    h2.sendCmd('java -jar App.jar')
    h3.sendCmd('java -jar App.jar')
    h4.sendCmd('java -jar App.jar')
    h5.sendCmd('java -jar App.jar')
    h6.sendCmd('java -jar App.jar')
    h7.sendCmd('java -jar App.jar')
    h8.sendCmd('java -jar App.jar')
    h9.sendCmd('java -jar App.jar')
    h10.sendCmd('java -jar App.jar')
    h11.sendCmd('java -jar App.jar')

    print h1.waitOutput()
    print h2.waitOutput()
    print h3.waitOutput()
    print h4.waitOutput()
    print h5.waitOutput()
    print h6.waitOutput()
    print h7.waitOutput()
    print h8.waitOutput()
    print h9.waitOutput()
    print h10.waitOutput()
    print h11.waitOutput()
    
    CLI(net)
    net.stop()

if __name__ == '__main__':
    setLogLevel( 'info' )
    myNetwork()

