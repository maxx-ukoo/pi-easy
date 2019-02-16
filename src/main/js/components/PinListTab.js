const React = require('react');
const ReactDOM = require('react-dom');
import { Icon, abel, List, Table, Menu, Label } from 'semantic-ui-react'

import axios from "axios";

class PinListTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = {pins: {}};
	}
	
	componentDidMount() {
	    axios.get('/api/pins')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.pins = response.data;
                this.setState(newState);
            });
	}

	render() {
	    let { pins } = this.state;
	    
	    
	    if (!Array.isArray(pins) || !pins.length) {
            return  <Label>Lera privet</Label>
        } else {
            return (
             <Table celled>
    <Table.Header>
      <Table.Row>
        <Table.HeaderCell>Header</Table.HeaderCell>
        <Table.HeaderCell>Header</Table.HeaderCell>
        <Table.HeaderCell>Header</Table.HeaderCell>
      </Table.Row>
    </Table.Header>

    <Table.Body>
     {pins.map(pin => (
          <Table.Row>
        		<Table.Cell>{pin.name}</Table.Cell>
        		<Table.Cell>{pin.address}</Table.Cell>
        		<Table.Cell>{pin.provider}</Table.Cell>
      	</Table.Row>
      	))}  
    </Table.Body>
  </Table>)
        }
    

	}
}

export default (PinListTab);