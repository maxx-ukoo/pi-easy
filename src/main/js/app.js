import React from 'react'
import { Label, Menu, Tab } from 'semantic-ui-react'
import { Container, Header } from 'semantic-ui-react'

import SystemTab from './components/SystemTab';
import PinListTab from './components/PinListTab';
import MQTTTab from './components/MQTTTab';

const panes = [
  {
    menuItem: { key: 'system', icon: 'info', content: 'System' },
    render: () => <Tab.Pane><SystemTab/></Tab.Pane>,
  },
  {
    menuItem: (
      <Menu.Item key='pinList'>
        Pin config<Label>15</Label>
      </Menu.Item>
    ),
    render: () => <Tab.Pane><PinListTab/></Tab.Pane>,
  },
  {
      menuItem: { key: 'mqtt', icon: 'info', content: 'MQTT' },
      render: () => <Tab.Pane><MQTTTab/></Tab.Pane>,
  },
]

const App = () => (
	<Container>
 		<Header as='h2' image='image' content='PI Control' />
 		<Tab panes={panes} />
 	</Container>
)

export default App