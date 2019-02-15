import React from 'react'
import { Label, Menu, Tab } from 'semantic-ui-react'
import { Container, Header } from 'semantic-ui-react'

const panes = [
  {
    menuItem: { key: 'users', icon: 'users', content: 'Users' },
    render: () => <Tab.Pane>Tab 1 Content</Tab.Pane>,
  },
  {
    menuItem: (
      <Menu.Item key='messages'>
        Messages<Label>15</Label>
      </Menu.Item>
    ),
    render: () => <Tab.Pane>Tab 2 Content</Tab.Pane>,
  },
]

const App = () => (
	<Container>
 		<Header as='h2' image='/images/icons/school.png' content='PI Control' />
 		<Tab panes={panes} />
 	</Container>
)

export default App