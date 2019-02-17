import React from 'react'
import { Label, Menu, Tab } from 'semantic-ui-react'
import { Container, Header } from 'semantic-ui-react'

import SystemTab from './components/SystemTab';
import PinListTab from './components/PinListTab';

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
]

const App = () => (
	<Container>
 		<Header as='h2' image='static/images/raspberrypi.png' content='PI Control' />
 		<Tab panes={panes} />
 	</Container>
)

export default App