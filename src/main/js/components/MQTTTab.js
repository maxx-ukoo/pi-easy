const React = require('react');
const ReactDOM = require('react-dom');
import { Container, Header, Form, Button, Modal, Dropdown, Label, List } from 'semantic-ui-react'

import axios from "axios";
import { findIndex } from 'lodash';

class MQTTTab extends React.Component {

	constructor(props) {
		super(props);
		this.state = {mqttlist: [], selectedItem: {}};
	}
	
	componentDidMount() {
	    axios.get('/api/mqtt/config')
            .then(response => {
                let newState = Object.assign({}, this.state);
                newState.mqttlist = response.data;
                this.setState(newState);
            });
	}
	handleChange = name => data => {
    	if (data) {
    	    let newState = Object.assign({}, this.state);
    	    newState.selectedItem[name] = data.target.value;
        	this.setState( newState );
     	}
    }

	onDropDownChange = (e, data) => {
		let idx = findIndex(this.state.mqttlist, {publisherId: data.value});
        this.setState({ selectedItem: this.state.mqttlist[idx] });
    }
    
    onUpdate = (e, data) => {
        const payload = {
            mqttConfig: this.state.selectedItem
        };
    
        axios.post('/api/mqtt/config', JSON.stringify(payload),{
            headers: {
                'Content-Type': 'application/json',
            }
        }).then(response => {
            let newState = Object.assign({}, this.state);
            newState.mqttlist = response.data;
            this.setState(newState);
        });
    }
    
    onRemove = (e, data) => {
        const payload = {
            mqttConfig: this.state.selectedItem
        };
        
        let { selectedItem } = this.state
        if (selectedItem && selectedItem.id) {
    	        axios.delete('/api/mqtt/config/' + selectedItem.id, ).then(response => {
	            let newState = Object.assign({}, this.state);
	            newState.mqttlist = response.data;
	            this.setState(newState);
	        });
	        this.setState({ selectedItem: {} })
	     }
    } 

	render() {
	    let { mqttlist, selectedItem } = this.state;
        let stateOptions = [];
        if (mqttlist) {
            mqttlist.map(node => {
                stateOptions.push(
                        { key: node.publisherId, value: node.publisherId, text: node.publisherId }
                );
            })
        }

        return (
            <Container text>
                <List divided selection>
                     <List.Item>
                        <Label horizontal>
                            MQTT brockers:
                        </Label>
                        <Dropdown selection options={stateOptions} onChange={this.onDropDownChange} placeholder='Select brocker...'/>
                        <Button primary>Add</Button>
                    </List.Item>
                </List>
                <Header as='h2'>Header</Header>
                <Form>
                   <Form.Field>
                      <label>Publisher Id</label>
                      <input placeholder='publisherId' value={selectedItem.publisherId} onChange={this.handleChange('publisherId')}/>
                   </Form.Field>
                   <Form.Field>
                      <label>Host</label>
                      <input placeholder='Host' value={selectedItem.host} onChange={this.handleChange('host')}/>
                   </Form.Field>
                   <Form.Field>
                      <label>Port</label>
                      <input placeholder='Port' value={selectedItem.port} onChange={this.handleChange('port')}/>
                   </Form.Field>
                   <Button type='submit' onClick={this.onUpdate} >Save</Button>
                   <Button type='submit' onClick={this.onRemove} >Remove</Button>
                </Form>
            </Container>
        )

	}
}

export default (MQTTTab);